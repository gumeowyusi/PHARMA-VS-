package com.poly.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.poly.entity.HoaDon;
import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.HoaDonChiTietId;
import com.poly.entity.KhachHang;
import com.poly.entity.OrderRequest;
import com.poly.entity.ReportRevenueStatistics;
import com.poly.entity.SanPham;
import com.poly.entity.Users;
import com.poly.entity.GioHang;
import com.poly.repository.HoaDonChiTietRepository;
import com.poly.repository.HoaDonRepository;
import com.poly.repository.SanPhamRepository;

@Service
public class HoaDonService {
	@Autowired
	UserService userService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	SanPhamRepository sanPhamRepository;
	@Autowired
	HoaDonRepository hoaDonRepository;
	@Autowired
	HoaDonChiTietRepository hoaDonChiTietRepository;
	@Autowired
	GioHangChiTietService gioHangChiTietService;
	@Autowired
	EmailService emailService;
	@Autowired
	VoucherService voucherService;
	@Autowired
	CartService cartService;
	@Autowired
	DiemTichLuyService diemTichLuyService;

	private boolean isGuest(OrderRequest orderRequest) {
		return orderRequest.getUserId() == null || "GUEST".equalsIgnoreCase(orderRequest.getUserId());
	}

	public Integer add(OrderRequest orderRequest) throws RuntimeException {
		try {
			List<HoaDonChiTiet> listHoaDonChiTiets = new ArrayList<>();
			Users users = null;
			KhachHang khachHang = null;
			if (!isGuest(orderRequest)) {
				users = userService.getUserById(orderRequest.getUserId());
			} else {
				GioHang cart = cartService.getCartById(orderRequest.getCartId());
				khachHang = cart.getKhachHang();
				if (khachHang == null) {
					throw new IllegalArgumentException("Thiếu thông tin khách hàng cho đơn hàng khách lẻ");
				}
			}
			orderRequest.getOrderItems().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getProductId());
				if (item.getQuantity() > sanPham.getSoluong()) {
					throw new RuntimeException("Số lượng tồn kho của sản phẩm " + sanPham.getTenSanpham());
				}
			});

			boolean isPayOS = "BANK_TRANSFER".equalsIgnoreCase(orderRequest.getPaymentMethod());
			String giaoHang = orderRequest.getDeliveryMethod() == 1 ? "Giao hàng tiêu chuẩn" : "Giao hàng nhanh";
			String pm = isPayOS ? "PayOS (Chuyển khoản)" : "COD (Tiền mặt)";
			String giaohangWithPayment = giaoHang + " | Thanh toán: " + pm;
			// PayOS: chờ thanh toán online → "awaiting_payment"; COD → auto "confirmed"
			String initialStatus = isPayOS ? "awaiting_payment" : "confirmed";

			HoaDon hoaDon = new HoaDon(users, new Date(), initialStatus, orderRequest.getAddress(), giaohangWithPayment);
			if (khachHang != null) {
				hoaDon.setKhachHang(khachHang);
			}
			if (orderRequest.getVoucherCode() != null && !orderRequest.getVoucherCode().isBlank()) {
				hoaDon.setVoucherCode(orderRequest.getVoucherCode());
				hoaDon.setVoucherDiscountAmount(orderRequest.getVoucherDiscountAmount() != null ? orderRequest.getVoucherDiscountAmount() : 0.0);
			}
			hoaDonRepository.save(hoaDon);
			orderRequest.getOrderItems().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getProductId());
				HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet(
						new HoaDonChiTietId(hoaDon.getIdHoadon(), sanPham.getIdSanpham()), hoaDon, sanPham,
						item.getQuantity(), sanPham.getGia(), sanPham.getGiamgia());
				listHoaDonChiTiets.add(hoaDonChiTietRepository.save(hoaDonChiTiet));

				sanPham.setSoluong(sanPham.getSoluong() - item.getQuantity());
				sanPhamRepository.save(sanPham);

				gioHangChiTietService.delete(orderRequest.getCartId(), item.getProductId());

			});
			try {
				String toEmail = null;
				if (users != null) toEmail = users.getIdUser();
				else if (khachHang != null && khachHang.getEmail() != null && !khachHang.getEmail().isBlank())
					toEmail = khachHang.getEmail();
				if (toEmail != null) {
					emailService.sendOrderConfirmationEmail(
							toEmail,
							"[MEDISALE] Xác nhận đơn hàng #" + hoaDon.getIdHoadon(),
							hoaDon, listHoaDonChiTiets, orderRequest.getDeliveryPrice());
				}
			} catch (Exception ignored) {
			}
			// Record voucher usage
			if (orderRequest.getVoucherCode() != null && !orderRequest.getVoucherCode().isBlank() && users != null) {
				try {
					double subtotal = orderRequest.getOrderItems().stream()
							.mapToDouble(i -> i.getPrice() * (1 - i.getDiscount() / 100.0) * i.getQuantity()).sum();
					voucherService.increaseUsageOnOrder(hoaDon, orderRequest.getVoucherCode(), subtotal, users.getIdUser());
				} catch (Exception ignored) {}
			}
			// Deduct loyalty points if user chose to use them
			int pointsToUse = orderRequest.getPointsToUse();
			if (pointsToUse > 0 && users != null) {
				try {
					boolean deducted = diemTichLuyService.redeemPoints(
							users.getIdUser(), pointsToUse,
							"Dùng điểm thanh toán đơn hàng #" + hoaDon.getIdHoadon());
					if (deducted) hoaDon.setPointsUsed(pointsToUse);
					hoaDonRepository.save(hoaDon);
				} catch (Exception ignored) {}
			}
			// Award loyalty points immediately for COD orders (PayOS handled in markOrderAsPaid)
			// Calculate total from orderRequest items to avoid lazy-loading issues
			if (!isPayOS) {
				try {
					double itemsTotal = orderRequest.getOrderItems().stream()
							.mapToDouble(i -> i.getPrice() * (1 - i.getDiscount() / 100.0) * i.getQuantity()).sum();
					double voucherDisc = orderRequest.getVoucherDiscountAmount() != null ? orderRequest.getVoucherDiscountAmount() : 0;
					double pointsDisc = pointsToUse;
					double netTotal = Math.max(0, itemsTotal - voucherDisc - pointsDisc);
					awardPointsForOrder(hoaDon, netTotal);
				} catch (Exception ignored) {}
			}
			return hoaDon.getIdHoadon();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Đã có lỗi xảy ra!");
		}

	}

	public Page<HoaDon> getAllHoaDonByIdUser(String email, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao", "idHoadon").descending());
		return hoaDonRepository.findByUsers_idUser(email, pageable);
	}

	public HoaDon getHoaDonById(Integer id) {
		return hoaDonRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Mã hóa đơn không tồn tại!"));
	}

	/** Gọi khi PayOS xác nhận thanh toán thành công */
	public void markOrderAsPaid(Integer orderId) {
		HoaDon hoaDon = getHoaDonById(orderId);
		if ("awaiting_payment".equals(hoaDon.getTrangthai())) {
			hoaDon.setTrangthai("paid");
			hoaDonRepository.save(hoaDon);
			// Award loyalty points for PayOS orders on payment confirmation
			try { awardPointsForOrder(hoaDon); } catch (Exception ignored) {}
			// Gửi email xác nhận
			try {
				List<HoaDonChiTiet> items = hoaDon.getHoaDonChiTiets();
				double deliveryPrice = 0;
				if (hoaDon.getGiaohang() != null && hoaDon.getGiaohang().contains("nhanh")) deliveryPrice = 50000;
				else if (hoaDon.getGiaohang() != null) deliveryPrice = 20000;
				String toEmail = null;
				if (hoaDon.getUsers() != null) toEmail = hoaDon.getUsers().getIdUser();
				else if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getEmail() != null)
					toEmail = hoaDon.getKhachHang().getEmail();
				if (toEmail != null) {
					emailService.sendOrderConfirmationEmail(
							toEmail,
							"[MEDISALE] Xác nhận đơn hàng #" + orderId + " - Đã thanh toán PayOS",
							hoaDon, items, deliveryPrice);
				}
			} catch (Exception ignored) {}
		}
	}

	public void cancelOrder(Integer id) {
		HoaDon hoaDon = getHoaDonById(id);
		hoaDon.setTrangthai("cancel");
		hoaDonRepository.save(hoaDon);

		hoaDon.getHoaDonChiTiets().forEach(item -> {
			SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
			sanPham.setSoluong(sanPham.getSoluong() + item.getSoluong());
			sanPhamRepository.save(sanPham);
		});
		// Voucher usage kept as-is on cancel (no rollback)
	}

	public Page<HoaDon> getAllHoaDon(int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao", "idHoadon").descending());
		return hoaDonRepository.findAll(pageable);
	}

	public Page<HoaDon> getAllHoaDonByStatus(String status, int pageNumber, int limit) {
		PageRequest pageable = PageRequest.of(pageNumber, limit, Sort.by("ngaytao", "idHoadon").descending());
		return hoaDonRepository.findByTrangthai(status, pageable);
	}

	public java.util.Map<String, Long> getOrderCounts() {
		java.util.Map<String, Long> counts = new java.util.LinkedHashMap<>();
		long total = hoaDonRepository.count();
		counts.put("all", total);
		for (String s : new String[]{"awaiting_payment", "pending", "confirmed", "ondelivery", "received", "cancel"}) {
			counts.put(s, hoaDonRepository.countByTrangthai(s));
		}
		return counts;
	}

	public String updateHoadon(Integer id, String action) throws IllegalArgumentException {
		HoaDon hoaDon = getHoaDonById(id);
		if ("APPROVE".equals(action)) {
			// pending/paid → confirmed (xác nhận đơn, chưa giao)
			hoaDon.setTrangthai("confirmed");
			hoaDonRepository.save(hoaDon);
			return "Đã xác nhận đơn hàng #" + id + "!";
		} else if ("SHIP".equals(action)) {
			// confirmed → ondelivery
			if (hoaDon.getTrangthai().equals("cancel")) {
				hoaDon.getHoaDonChiTiets().forEach(item -> {
					SanPham sp = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
					sp.setSoluong(sp.getSoluong() - item.getSoluong());
					sanPhamRepository.save(sp);
				});
			}
			hoaDon.setTrangthai("ondelivery");
			hoaDonRepository.save(hoaDon);
			return "Đã chuyển sang đang giao hàng cho đơn #" + id + "!";
		} else if ("CONFIRM".equals(action)) {
			// ondelivery → received
			hoaDon.setTrangthai("received");
			hoaDonRepository.save(hoaDon);
			awardPointsForOrder(hoaDon);
			return "Đã xác nhận giao hàng thành công cho đơn #" + id + "!";
		} else if ("CANCEL".equals(action)) {
			hoaDon.setTrangthai("cancel");
			hoaDonRepository.save(hoaDon);
			hoaDon.getHoaDonChiTiets().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
				sanPham.setSoluong(sanPham.getSoluong() + item.getSoluong());
				sanPhamRepository.save(sanPham);
			});
			// Voucher usage kept as-is on cancel (no rollback)
			return "Đã hủy đơn hàng #" + id + "!";
		} else {
			// legacy fallback
			hoaDon.setTrangthai("ondelivery");
			hoaDonRepository.save(hoaDon);
			return "Đã cập nhật trạng thái đơn hàng #" + id + "!";
		}
	}

	/** Award points from a known net order total (avoids lazy-loading the item collection). */
	private void awardPointsForOrder(HoaDon hoaDon, double netTotal) {
		if (hoaDon.getUsers() == null) return;
		try {
			int points = diemTichLuyService.calculatePointsForOrder(netTotal);
			if (points > 0) {
				diemTichLuyService.addPoints(
						hoaDon.getUsers().getIdUser(), points,
						"Tích điểm đơn hàng #" + hoaDon.getIdHoadon()
								+ " (" + String.format("%,.0f", Math.max(0, netTotal)) + " VNĐ)",
						hoaDon.getIdHoadon());
			}
		} catch (Exception ignored) {}
	}

	/** Award points by reloading items from DB — used for PayOS after async payment confirmation. */
	private void awardPointsForOrder(HoaDon hoaDon) {
		if (hoaDon.getUsers() == null) return;
		try {
			List<HoaDonChiTiet> items = hoaDonChiTietRepository.findByHoaDon_IdHoadon(hoaDon.getIdHoadon());
			double total = items.stream()
					.mapToDouble(item -> item.getGiamgia() == 0
							? item.getGia() * item.getSoluong()
							: item.getGia() * (100 - item.getGiamgia()) / 100.0 * item.getSoluong())
					.sum();
			double voucherDisc = hoaDon.getVoucherDiscountAmount() != null ? hoaDon.getVoucherDiscountAmount() : 0;
			double pointsDisc = hoaDon.getPointsUsed() != null ? hoaDon.getPointsUsed() : 0;
			double netTotal = Math.max(0, total - voucherDisc - pointsDisc);
			awardPointsForOrder(hoaDon, netTotal);
		} catch (Exception ignored) {}
	}

	public long countReceivedOrders() {
		return hoaDonRepository.countReceivedOrders();
	}

	public ReportRevenueStatistics thongKeDoanhThuTheoLoai(Integer loaiId) {
		return hoaDonRepository.thongKeDoanhThuTheoLoai(loaiId);
	}

	public long[] getKpiCounts() {
		return new long[]{
			hoaDonRepository.countReceivedOrders(),
			hoaDonRepository.countOnDeliveryOrders(),
			hoaDonRepository.countAllUsers()
		};
	}

	public Double tongDoanhThuToanBo() {
		return hoaDonRepository.tongDoanhThuToanBo();
	}

	public long[] doanhThuTheoThang(int nam) {
		java.util.List<Object[]> rows = hoaDonRepository.doanhThuTheoThang(nam);
		long[] result = new long[12];
		for (Object[] row : rows) {
			int thang = ((Number) row[0]).intValue() - 1;
			result[thang] = ((Number) row[1]).longValue();
		}
		return result;
	}

	public java.util.List<Object[]> topSanPhamBanChay() {
		return hoaDonRepository.topSanPhamBanChay();
	}
}
