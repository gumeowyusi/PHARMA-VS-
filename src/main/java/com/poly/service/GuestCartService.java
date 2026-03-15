package com.poly.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.poly.entity.GioHang;
import com.poly.entity.GioHangChiTiet;
import com.poly.entity.GioHangChiTietId;
import com.poly.entity.KhachHang;
import com.poly.entity.SanPham;
import com.poly.repository.GioHangChiTietRepository;
import com.poly.repository.GioHangRepository;
import com.poly.repository.KhachHangRepository;

@Service
public class GuestCartService {
    private final GioHangRepository gioHangRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final SanPhamService sanPhamService;
    private final KhachHangRepository khachHangRepository;

    public GuestCartService(GioHangRepository gioHangRepository,
                            GioHangChiTietRepository gioHangChiTietRepository,
                            SanPhamService sanPhamService,
                            KhachHangRepository khachHangRepository) {
        this.gioHangRepository = gioHangRepository;
        this.gioHangChiTietRepository = gioHangChiTietRepository;
        this.sanPhamService = sanPhamService;
        this.khachHangRepository = khachHangRepository;
    }

    public GioHang ensureGuestCart(Integer cartId) {
        if (cartId != null) {
            Optional<GioHang> gh = gioHangRepository.findById(cartId);
            if (gh.isPresent()) return gh.get();
        }
        GioHang gh = new GioHang();
        gh.setUsers(null);
        gh.setKhachHang(null);
        return gioHangRepository.save(gh);
    }

    public List<GioHangChiTiet> getItems(Integer cartId) {
        return gioHangChiTietRepository.findByGioHang_IdGiohang(cartId);
    }

    public void addItem(Integer cartId, int productId, int quantity) {
    GioHang cart = gioHangRepository.findById(cartId)
        .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));
        SanPham sp = sanPhamService.getSanPhamById(productId);
        GioHangChiTietId id = new GioHangChiTietId(cartId, productId);
        GioHangChiTiet existing = gioHangChiTietRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setSoluong(existing.getSoluong() + quantity);
            gioHangChiTietRepository.save(existing);
        } else {
            GioHangChiTiet row = new GioHangChiTiet(id, cart, sp, quantity);
            gioHangChiTietRepository.save(row);
        }
    }

    public void updateItem(Integer cartId, int productId, int quantity) {
    GioHang cart = gioHangRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));
    GioHangChiTietId id = new GioHangChiTietId(cart.getIdGiohang(), productId);
        GioHangChiTiet row = gioHangChiTietRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng"));
        row.setSoluong(quantity);
        gioHangChiTietRepository.save(row);
    }

    public void removeItem(Integer cartId, int productId) {
        GioHangChiTietId id = new GioHangChiTietId(cartId, productId);
        gioHangChiTietRepository.deleteById(id);
    }

    public void clearCart(Integer cartId) {
        gioHangChiTietRepository.deleteByCartId(cartId);
    }

    public KhachHang ensureGuestCustomer(String hoten, String sdt, String email, String diaChi) {
        return khachHangRepository.findBySdt(sdt).orElseGet(() -> {
            KhachHang k = new KhachHang();
            k.setHoten(hoten);
            k.setSdt(sdt);
            k.setEmail(email);
            k.setDiaChi(diaChi);
            k.setTrangThai(true);
            k.setPhanLoai("GUEST");
            return khachHangRepository.save(k);
        });
    }

    public void attachCustomerToCart(Integer cartId, KhachHang khachHang) {
        GioHang cart = gioHangRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Giỏ hàng không tồn tại"));
        cart.setKhachHang(khachHang);
        gioHangRepository.save(cart);
    }
}
