package com.poly.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.entity.CartItemResponse;
import com.poly.entity.OrderItemRequest;
import com.poly.entity.OrderRequest;
import com.poly.entity.SanPham;
import com.poly.service.GuestCartService;
import com.poly.service.HoaDonService;
import com.poly.service.SanPhamService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class GuestCartController {

    private final GuestCartService guestCartService;
    private final SanPhamService sanPhamService;
    private final HoaDonService hoaDonService;

    public GuestCartController(GuestCartService guestCartService, SanPhamService sanPhamService,
            HoaDonService hoaDonService) {
        this.guestCartService = guestCartService;
        this.sanPhamService = sanPhamService;
        this.hoaDonService = hoaDonService;
    }

    private static final String CART_COOKIE = "guest_cart_id";

    @ResponseBody
    @PostMapping("/guest/cart")
    public ResponseEntity<Object> ensureCart(@CookieValue(value = CART_COOKIE, required = false) Integer cartId,
            HttpServletResponse response) {
        var cart = guestCartService.ensureGuestCart(cartId);
        if (cartId == null || !cart.getIdGiohang().equals(cartId)) {
            Cookie c = new Cookie(CART_COOKIE, String.valueOf(cart.getIdGiohang()));
            c.setHttpOnly(true);
            c.setPath("/");
            // c.setSecure(true); // enable if https
            c.setMaxAge(60 * 60 * 24 * 30); // 30 days
            response.addCookie(c);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("cartId", cart.getIdGiohang());
        return ResponseEntity.ok(res);
    }

    @ResponseBody
    @GetMapping("/guest/cartItem")
    public ResponseEntity<Object> getItems(@CookieValue(value = CART_COOKIE, required = false) Integer cartId,
            HttpServletResponse servletResponse) {
        AtomicInteger counter = new AtomicInteger(1);
        Map<String, List<CartItemResponse>> res = new HashMap<>();
        Integer effectiveCartId = cartId;
        if (effectiveCartId == null) {
            var cart = guestCartService.ensureGuestCart(null);
            Cookie c = new Cookie(CART_COOKIE, String.valueOf(cart.getIdGiohang()));
            c.setHttpOnly(true);
            c.setPath("/");
            c.setMaxAge(60 * 60 * 24 * 30);
            servletResponse.addCookie(c);
            effectiveCartId = cart.getIdGiohang();
        }

        var items = guestCartService.getItems(effectiveCartId).stream()
                .filter(item -> {
                    SanPham sp = sanPhamService.getSanPhamById(item.getSanPham().getIdSanpham());
                    return sp.getSoluong() > 0;
                })
                .map(item -> new CartItemResponse(
                        counter.getAndIncrement(),
                        item.getGioHang().getIdGiohang(),
                        item.getSanPham().getIdSanpham(),
                        item.getSanPham().getTenSanpham(),
                        item.getSanPham().getGia(),
                        item.getSanPham().getGiamgia(),
                        item.getSanPham().getSoluong(),
                        item.getSanPham().getHinh(),
                        item.getSoluong()))
                .collect(Collectors.toList());
        res.put("cartItems", items);
        return ResponseEntity.ok(res);
    }

    record GuestAddItemRequest(int productId, int quantity) {
    }

    @ResponseBody
    @PostMapping("/guest/cartItem")
    public ResponseEntity<Object> addItem(@CookieValue(value = CART_COOKIE) Integer cartId,
            @RequestBody GuestAddItemRequest req) {
        Map<String, String> res = new HashMap<>();
        try {
            guestCartService.addItem(cartId, req.productId(), req.quantity());
            res.put("message", "Đã thêm sản phẩm vào giỏ hàng!");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("message", "Đã có lỗi truy vấn!");
            return ResponseEntity.status(404).body(res);
        }
    }

    @ResponseBody
    @PutMapping("/guest/cartItem")
    public ResponseEntity<Object> updateItem(@CookieValue(value = CART_COOKIE) Integer cartId,
            @RequestParam("cartItemId") Integer cartItemId,
            @RequestBody GuestAddItemRequest req) {
        Map<String, String> res = new HashMap<>();
        try {
            guestCartService.updateItem(cartId, req.productId(), req.quantity());
            res.put("message", "Đã cập nhật số lượng!");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("message", "Đã có lỗi truy vấn!");
            return ResponseEntity.status(404).body(res);
        }
    }

    @ResponseBody
    @DeleteMapping("/guest/cartItem")
    public ResponseEntity<Object> removeItem(@CookieValue(value = CART_COOKIE) Integer cartId,
            @RequestParam("productId") Integer productId) {
        Map<String, String> res = new HashMap<>();
        try {
            guestCartService.removeItem(cartId, productId);
            res.put("message", "Đã xóa sản phẩm!");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("message", "Đã có lỗi truy vấn!");
            return ResponseEntity.status(404).body(res);
        }
    }

    record GuestCheckoutRequest(String hoten, String sdt, String email, String address, int deliveryMethod,
        double deliveryPrice, String paymentMethod, List<OrderItemRequest> orderItems) {
    }

    @ResponseBody
    @PostMapping("/guest/checkout")
    public ResponseEntity<Object> checkout(@CookieValue(value = CART_COOKIE) Integer cartId,
            @RequestBody GuestCheckoutRequest req) {
        Map<String, Object> res = new HashMap<>();
        try {
            var kh = guestCartService.ensureGuestCustomer(req.hoten(), req.sdt(), req.email(), req.address());
            guestCartService.attachCustomerToCart(cartId, kh);
        OrderRequest orderRequest = new OrderRequest(cartId, "GUEST", req.address(), req.deliveryMethod(),
            req.deliveryPrice(), req.paymentMethod(), req.orderItems());
            Integer orderId = hoaDonService.add(orderRequest);
            guestCartService.clearCart(cartId);
            res.put("message", "Đặt hàng thành công (khách lẻ)! Nhân viên sẽ liên hệ xác nhận.");
            res.put("orderId", orderId);
            return ResponseEntity.ok(res);
        } catch (RuntimeException e) {
            res.put("message", "Số lượng tồn kho không đủ: " + e.getMessage());
            return ResponseEntity.status(422).body(res);
        } catch (Exception e) {
            res.put("message", "Đã có lỗi truy vấn!");
            return ResponseEntity.status(404).body(res);
        }
    }
}
