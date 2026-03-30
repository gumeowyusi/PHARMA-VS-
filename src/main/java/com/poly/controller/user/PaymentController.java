package com.poly.controller.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.entity.GioHang;
import com.poly.entity.GioHangChiTiet;
import com.poly.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

@Controller
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PayOS payOS;
    private final CartService cartService;

    public PaymentController(PayOS payOS, CartService cartService) {
        this.payOS = payOS;
        this.cartService = cartService;
    }

    record CreateBankTransferRequest(int cartId, int deliveryMethod, double deliveryPrice, String note) {}

    @ResponseBody
    @PostMapping("/api/payments/bank-transfer")
    public ResponseEntity<?> createBankTransfer(@RequestBody CreateBankTransferRequest req, HttpServletRequest request) {
        try {
            GioHang cart = cartService.getCartById(req.cartId());
            if (cart == null) return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy giỏ hàng"));
            List<GioHangChiTiet> items = cart.getGioHangChiTiets();
            if (items == null || items.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("message", "Giỏ hàng trống"));
            }

            int tempTotal = items.stream().mapToInt(ct -> {
                int unit = ct.getSanPham().getGia() * (100 - ct.getSanPham().getGiamgia()) / 100;
                return unit * ct.getSoluong();
            }).sum();
            long amount = Math.max(0, Math.round(tempTotal + req.deliveryPrice()));

            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(Math.max(0, currentTimeString.length() - 10)));

            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath();

            List<ItemData> payItems = items.stream().map(ct -> ItemData.builder()
                    .name(ct.getSanPham().getTenSanpham())
                    .quantity(ct.getSoluong())
                    .price(ct.getSanPham().getGia() * (100 - ct.getSanPham().getGiamgia()) / 100)
                    .build()).toList();

        // PayOS description: uppercase A-Z, digits 0-9, spaces only, max 25 chars
        String codeStr = String.valueOf(orderCode);
        String shortDescription = "THANH TOAN " + codeStr;
        if (shortDescription.length() > 25) {
            shortDescription = "DH " + codeStr;
        }
        if (shortDescription.length() > 25) {
            shortDescription = codeStr.substring(Math.max(0, codeStr.length() - 25));
        }

        PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount((int) amount)
            .description(shortDescription)
                    .returnUrl(baseUrl + "/cart?pay=success&code=" + orderCode)
                    .cancelUrl(baseUrl + "/cart?pay=cancel&code=" + orderCode)
                    .items(payItems)
                    .build();

            log.info("[PayOS] Creating link: orderCode={}, amount={}, desc='{}'", orderCode, amount, shortDescription);
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            log.info("[PayOS] Link created: {}", data.getCheckoutUrl());

            return ResponseEntity.ok(Map.of(
                "orderCode", orderCode,
                "checkoutUrl", data.getCheckoutUrl()
            ));
        } catch (Exception e) {
            log.error("[PayOS] Failed to create payment link: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Không tạo được liên kết thanh toán: " + e.getMessage()));
        }
    }

    @ResponseBody
    @GetMapping("/api/payments/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam("orderCode") long orderCode) {
        try {
            var info = payOS.getPaymentLinkInformation(orderCode);
            String status = info.getStatus();
            return ResponseEntity.ok(Map.of("status", status));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy thông tin thanh toán"));
        }
    }
}
