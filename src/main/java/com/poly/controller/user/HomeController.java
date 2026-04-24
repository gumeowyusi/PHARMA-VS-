package com.poly.controller.user;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.HoaDon;
import com.poly.entity.SanPham;
import com.poly.entity.TinTuc;
import com.poly.entity.Users;
import com.poly.service.DiemTichLuyService;
import com.poly.service.HoaDonService;
import com.poly.service.LoaiService;
import com.poly.service.NewsAggregatorService;
import com.poly.service.SanPhamService;
import com.poly.service.TinTucService;
import com.poly.service.UserService;
import com.poly.service.CurrentUserService;
import com.poly.service.WishlistService;

import java.util.List;

@Controller
public class HomeController {
	@Autowired
	UserService userService;
	@Autowired
	LoaiService loaiService;
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	CurrentUserService currentUserService;
	@Autowired
	HoaDonService hoaDonService;
	@Autowired
	WishlistService wishlistService;
	@Autowired
	TinTucService tinTucService;
	@Autowired
	NewsAggregatorService newsAggregatorService;
	@Autowired
	DiemTichLuyService diemTichLuyService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("sanphams", sanPhamService.getAllSanPham(0, 12));
		model.addAttribute("listSPGiamGia", sanPhamService.getAllSanPhamGiamGia(0, 12));
		return "user/home";
	}

	@GetMapping("/san-pham-moi")
	public String newProducts(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		Page<SanPham> sanPhamPage = sanPhamService.getAllSanPham(page, 12);
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("sanphams", sanPhamPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", sanPhamPage.getTotalPages());
		return "user/newProducts";
	}

	@GetMapping("/khuyen-mai")
	public String promotions(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		Page<SanPham> salePage = sanPhamService.getAllSanPhamGiamGia(page, 12);
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("sanphams", salePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", salePage.getTotalPages());
		return "user/promotions";
	}

	@GetMapping("/tin-tuc")
	public String news(@RequestParam(required = false) String theLoai, Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		model.addAttribute("featuredProducts", sanPhamService.getAllSanPham(0, 3).getContent());
		List<TinTuc> adminPosts = theLoai != null && !theLoai.isBlank()
				? tinTucService.getByTheLoai(theLoai)
				: tinTucService.getAllPublished();
		model.addAttribute("adminPosts", adminPosts);
		model.addAttribute("activeTheLoai", theLoai != null ? theLoai : "all");
		try {
			model.addAttribute("externalNews", newsAggregatorService.getLatestNews(15));
		} catch (Exception e) {
			model.addAttribute("externalNews", java.util.Collections.emptyList());
		}
		return "user/news";
	}

	@GetMapping("/tin-tuc/{id}")
	public String newsDetail(@PathVariable Long id, Model model) {
		try {
			TinTuc tinTuc = tinTucService.getById(id);
			tinTucService.incrementView(id);
			model.addAttribute("tinTuc", tinTuc);
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("featuredProducts", sanPhamService.getAllSanPham(0, 3).getContent());
			model.addAttribute("relatedPosts", tinTucService.getByTheLoai(tinTuc.getTheLoai() != null ? tinTuc.getTheLoai() : ""));
			return "user/newsDetail";
		} catch (Exception e) {
			return "redirect:/tin-tuc";
		}
	}

	@GetMapping("/lien-he")
	public String contact(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/contact";
	}

	@PostMapping("/lien-he")
	public String submitContact(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "phone", required = false) String phone,
			@RequestParam(name = "message", required = false) String message,
			RedirectAttributes redirectAttributes) {
		String displayName = (name == null || name.isBlank()) ? "bạn" : name.trim();
		redirectAttributes.addFlashAttribute("successMessage",
				"Cảm ơn " + displayName + "! Nhà thuốc đã nhận thông tin và sẽ liên hệ sớm.");
		redirectAttributes.addFlashAttribute("lastPhone", phone);
		redirectAttributes.addFlashAttribute("lastMessage", message);
		return "redirect:/lien-he";
	}

	@GetMapping("/loai-all")
	public String loaiAll(Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 100));
		return "user/categoryAll";
	}

	@GetMapping("/signin")
	public String signin(@ModelAttribute("user") Users user, Model model) {
		var current = currentUserService.getCurrentUser();
		if (current.isPresent()) {
			Users u = current.get();
			if (u.isAdmin()) return "redirect:/admin";
			if (u.isStaff()) return "redirect:/banhangtaiquay";
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signin";
	}

	@PostMapping("/signin")
	public String login(@ModelAttribute("user") Users user, Model model) {
		try {
			Users loggedIn = userService.login(user);
			if (loggedIn.isAdmin()) return "redirect:/admin";
			if (loggedIn.isStaff()) return "redirect:/banhangtaiquay";
			return "redirect:/";
		} catch (Exception e) {
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("errorMessage", e.getMessage());
			return "user/signin";
		}
	}

	@GetMapping("/signup")
	public String signup(@ModelAttribute("user") Users user, Model model) {
		if (currentUserService.getCurrentUser().isPresent()) {
			return "redirect:/";
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signup";
	}

	@GetMapping("/signout")
	public String signout() {
		userService.logout();
		return "redirect:/";
	}

	@PostMapping("/signup")
	public String regiter(@ModelAttribute("user") Users user, Model model) {
		try {
			userService.register(user);
			model.addAttribute("successMessage", "Tạo tài khoản thành công, vui lòng mở mail để kích hoạt tài khoản");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/signup";
	}

	@GetMapping("/active-account")
	public String resetPassword(@RequestParam(required = true, name = "token") String token,
			RedirectAttributes redirectAttributes) {
		try {
			userService.checkToken(token);
			redirectAttributes.addFlashAttribute("successMessage", "Tài khoản bạn đã được kích hoạt");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Token không hợp lệ hoặc đã hết hạn");
		}
		redirectAttributes.addFlashAttribute("loais", loaiService.getAllLoai(0, 5));
		return "redirect:/signin";
	}

	@ResponseBody
	@GetMapping("/image/{filename:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable(name = "filename") String filename) {
		File file = new File("c:/var/java5/images/" + filename);
		if (!file.exists() || !file.isFile()) {
			return ResponseEntity.status(404).body("File khong ton tai");
		}

		try {
			UrlResource resource = new UrlResource(file.toURI());
			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.status(404).body("File khong ton tai");
			}
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
					.body(resource);
		} catch (MalformedURLException ex) {
			return ResponseEntity.status(404).body("File khong ton tai");
		}
	}

	@GetMapping("/sanpham")
	public String sanpham(@RequestParam(name = "id") String id, Model model) {
		try {
			SanPham sanPham = sanPhamService.getSanPhamById(Integer.valueOf(id));
			model.addAttribute("sanpham", sanPham);
			model.addAttribute("sanphams", sanPhamService.getSanPhamByIdLoai(sanPham.getLoai().getIdLoai(), 0, 4)
					.filter(item -> item.getIdSanpham() != sanPham.getIdSanpham()));
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("luotMua", sanPhamService.getLuotMuaById(sanPham.getIdSanpham()));
			boolean wishlisted = currentUserService.getCurrentUser()
					.map(u -> wishlistService.isWishlisted(u.getIdUser(), sanPham.getIdSanpham()))
					.orElse(false);
			model.addAttribute("isWishlisted", wishlisted);
			return "user/productDetail";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/order")
	public String order(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		return currentUserService.getCurrentUser().map(u -> {
			Page<HoaDon> hoaDonPage = hoaDonService.getAllHoaDonByIdUser(u.getIdUser(), page, 4);
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("orders", hoaDonPage.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", hoaDonPage.getTotalPages());
			return "user/orderView";
		}).orElse("redirect:/");
	}

	@GetMapping("/orderDetail")
	public String orderDetail(@RequestParam(name = "id") String id, Model model) {
		try {
			AtomicReference<Double> tempPrice = new AtomicReference<>(0.0);
			List<SanPham> listSanPham = new ArrayList<>();
			HoaDon hoaDon = hoaDonService.getHoaDonById(Integer.valueOf(id));
			hoaDon.getHoaDonChiTiets().forEach(item -> {
				SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
				double price = (item.getGiamgia() == 0) ? item.getGia() * item.getSoluong()
						: (item.getGia() * (100 - item.getGiamgia()) / 100) * item.getSoluong();
				tempPrice.updateAndGet(v -> v + price);
				sanPham.setSoluong(item.getSoluong());
				sanPham.setGia(item.getGia());
				sanPham.setGiamgia(item.getGiamgia());
				listSanPham.add(sanPham);
			});
			String giaohang = hoaDon.getGiaohang() != null ? hoaDon.getGiaohang() : "";
			double deliveryPrice = giaohang.contains("nhanh") ? 50000 : 20000;
			double voucherDiscount = hoaDon.getVoucherDiscountAmount() != null ? hoaDon.getVoucherDiscountAmount() : 0.0;
			double finalTotal = Math.max(0, tempPrice.get() + deliveryPrice - voucherDiscount);
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("tempPrice", tempPrice.get());
			model.addAttribute("listSanPham", listSanPham);
			model.addAttribute("deliveryPrice", deliveryPrice);
			model.addAttribute("voucherDiscount", voucherDiscount);
			model.addAttribute("finalTotal", finalTotal);
			model.addAttribute("order", hoaDon);
			return "user/orderDetailView";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/api/order/{id}/bill")
	@ResponseBody
	public ResponseEntity<?> getOrderBill(@PathVariable int id) {
		try {
			Users currentUser = currentUserService.getCurrentUser().orElse(null);
			HoaDon hoaDon = hoaDonService.getHoaDonById(id);
			// Only the owner can view their bill
			if (currentUser == null || !hoaDon.getUsers().getIdUser().equals(currentUser.getIdUser())) {
				return ResponseEntity.status(403).body(java.util.Map.of("error", "Không có quyền truy cập"));
			}
			java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
			double subtotal = 0;
			int idx = 1;
			for (com.poly.entity.HoaDonChiTiet ct : hoaDon.getHoaDonChiTiets()) {
				double unitPrice = ct.getGiamgia() == 0
						? ct.getGia()
						: Math.round(ct.getGia() * (100 - ct.getGiamgia()) / 100.0);
				double lineTotal = unitPrice * ct.getSoluong();
				subtotal += lineTotal;
				com.poly.entity.SanPham sp = sanPhamService.getSanPhamById(ct.getId().getIdSanpham());
				java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
				item.put("no", idx++);
				item.put("name", sp != null ? sp.getTenSanpham() : "Sản phẩm #" + ct.getId().getIdSanpham());
				item.put("qty", ct.getSoluong());
				item.put("unitPrice", (long) unitPrice);
				item.put("lineTotal", (long) lineTotal);
				items.add(item);
			}
			String giaohang = hoaDon.getGiaohang() != null ? hoaDon.getGiaohang() : "";
			double deliveryPrice = giaohang.contains("nhanh") ? 50000 : 20000;
			double voucherDiscount = hoaDon.getVoucherDiscountAmount() != null ? hoaDon.getVoucherDiscountAmount() : 0;
			double total = Math.max(0, subtotal + deliveryPrice - voucherDiscount);
			// Extract payment method from giaohang field
			String paymentMethod = "COD (Tiền mặt)";
			if (giaohang.toLowerCase().contains("payos") || giaohang.toLowerCase().contains("chuyển khoản")) {
				paymentMethod = "Chuyển khoản (PayOS)";
			}
			String deliveryMethod = giaohang.contains("nhanh") ? "Giao hàng nhanh" : "Giao hàng tiêu chuẩn";
			java.util.Map<String, Object> bill = new java.util.LinkedHashMap<>();
			bill.put("orderId", hoaDon.getIdHoadon());
			bill.put("date", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(hoaDon.getNgaytao()));
			bill.put("customerName", currentUser.getHoten());
			bill.put("items", items);
			bill.put("subtotal", (long) subtotal);
			bill.put("deliveryPrice", (long) deliveryPrice);
			bill.put("deliveryMethod", deliveryMethod);
			bill.put("voucherCode", hoaDon.getVoucherCode());
			bill.put("voucherDiscount", (long) voucherDiscount);
			bill.put("total", (long) total);
			bill.put("paymentMethod", paymentMethod);
			bill.put("address", hoaDon.getDiachi());
			return ResponseEntity.ok(bill);
		} catch (Exception e) {
			return ResponseEntity.status(404).body(java.util.Map.of("error", "Không tìm thấy đơn hàng"));
		}
	}

	@PostMapping("/orderDetail")
	public String huyOrderDetail(@RequestParam(name = "id") String id, Model model) {
		try {
			hoaDonService.cancelOrder(Integer.valueOf(id));
			return "redirect:/order";
		} catch (Exception e) {
			return "redirect:/";
		}
	}

	@GetMapping("/setting")
	public String setting(Model model) {
		return currentUserService.getCurrentUser().map(u -> {
			model.addAttribute("user", userService.getUserById(u.getIdUser()));
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			return "user/settingView";
		}).orElse("redirect:/");
	}

	@PostMapping("/setting")
	public String setting(@RequestParam(name = "image", required = false) MultipartFile image,
			@ModelAttribute("user") Users updateUser, Model model) {
		return currentUserService.getCurrentUser().map(u -> {
			try {
				model.addAttribute("user", userService.updateProfile(u.getIdUser(), updateUser, image));
				model.addAttribute("loais", loaiService.getAllLoai(0, 5));
				model.addAttribute("successMessage", "Cập nhật tài khoản thành công");
			} catch (Exception e) {
				model.addAttribute("errorMessage", e.getMessage());
			}
			return "user/settingView";
		}).orElse("redirect:/");
	}

	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		return currentUserService.getCurrentUser().map(u -> {
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			return "user/changePasswordView";
		}).orElse("redirect:/");
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam(name = "currentPassword") String currentPassword,
			@RequestParam(name = "newPassword") String newPassword,
			@RequestParam(name = "newPasswordAgain") String newPasswordAgain, Model model) {
		Users currentUser = currentUserService.getCurrentUser().orElse(null);
		if (currentUser == null) return "redirect:/";
		
		boolean newPasswordEqualsNewPasswordAgain = newPassword.equals(newPasswordAgain);
		if (!newPasswordEqualsNewPasswordAgain) {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", "Mật khẩu mới và mật khẩu nhập lại không khớp!");
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			return "user/changePasswordView";
		}
		
		try {
			userService.changePassword(currentUser.getIdUser(), currentPassword, newPassword);
			model.addAttribute("successMessage", "Đổi mật khẩu thành công!");
		} catch (IllegalArgumentException e) {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", e.getMessage());
		} catch (Exception e) {
			model.addAttribute("currentPassword", currentPassword);
			model.addAttribute("newPassword", newPassword);
			model.addAttribute("newPasswordAgain", newPasswordAgain);
			model.addAttribute("errorMessage", "Lỗi không xác định: " + e.getMessage());
		}
		
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/changePasswordView";
	}

	@GetMapping("/forgot-password")
	public String forgotPassword(@ModelAttribute("user") Users user, Model model) {
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/forgotPassword";
	}

	@PostMapping("/forgot-password")
	public String postForgotPassword(@RequestParam(name = "idUser") String idUser, Model model) {
		try {
			userService.sendMailPass(idUser);
			model.addAttribute("successMessage", "Gửi mail thành công");
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		model.addAttribute("idUser", idUser);
		model.addAttribute("loais", loaiService.getAllLoai(0, 5));
		return "user/forgotPassword";
	}

	@PostMapping("/cance-account")
	public String canceAccount(@RequestParam(name = "idUser") String idUser, Model model) {
		if (currentUserService.getCurrentUser().isEmpty()) {
			return "redirect:/";
		}
		try {
			userService.canceAccount(idUser);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "user/settingView";
		}
		return "redirect:/signin";
	}

	@GetMapping("/history")
	public String history(@RequestParam(defaultValue = "0", name = "page") int page, Model model) {
		return currentUserService.getCurrentUser().map(u -> {
			Page<SanPham> sanphamPage = sanPhamService.findSanPhamByUser(u.getIdUser(), page, 4);
			model.addAttribute("sanphams", sanphamPage.getContent());
			model.addAttribute("loais", loaiService.getAllLoai(0, 5));
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", sanphamPage.getTotalPages());
			return "user/history";
		}).orElse("redirect:/");
	}
}
