package com.poly.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.entity.HoaDon;
import com.poly.entity.SanPham;
import com.poly.service.HoaDonService;
import com.poly.service.SanPhamService;

@Controller
public class HoaDonController {
	@Autowired
	HoaDonService hoaDonService;
	@Autowired
	SanPhamService sanPhamService;

	@GetMapping("/admin/hoadon")
	@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
	public String hoadonManager(Model model,
			@RequestParam(defaultValue = "0", name = "page") int page,
			@RequestParam(defaultValue = "", name = "status") String status) {
		Page<HoaDon> hoadonPage = status.isEmpty()
				? hoaDonService.getAllHoaDon(page, 10)
				: hoaDonService.getAllHoaDonByStatus(status, page, 10);
		model.addAttribute("hoadons", hoadonPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", hoadonPage.getTotalPages());
		model.addAttribute("currentStatus", status);
		model.addAttribute("counts", hoaDonService.getOrderCounts());
		return "admin/hoadon/hoadonManager";
	}

	@PostMapping("/admin/api/hoadon/{id}/status")
	@ResponseBody
	@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
	public ResponseEntity<?> updateOrderStatusAjax(@PathVariable int id, @RequestParam String action) {
		try {
			String msg = hoaDonService.updateHoadon(id, action);
			HoaDon updated = hoaDonService.getHoaDonById(id);
			return ResponseEntity.ok(Map.of("success", true, "message", msg, "newStatus", updated.getTrangthai()));
		} catch (Exception e) {
			return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@GetMapping("/admin/hoadon/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
	public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
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
			// Derive discount percent if missing but discountAmount present
			Integer effectiveDiscountPercent = hoaDon.getDiscountPercent();
			if (effectiveDiscountPercent == null && hoaDon.getDiscountAmount() != null && tempPrice.get() > 0) {
				effectiveDiscountPercent = (int) Math.round(hoaDon.getDiscountAmount() * 100.0 / tempPrice.get());
			}
			model.addAttribute("tempPrice", tempPrice.get());
			model.addAttribute("listSanPham", listSanPham);
			String gh = hoaDon.getGiaohang() != null ? hoaDon.getGiaohang() : "";
			model.addAttribute("deliveryPrice", gh.contains("nhanh") ? 50000 : 20000);
			model.addAttribute("order", hoaDon);
			model.addAttribute("derivedDiscountPercent", effectiveDiscountPercent);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/hoadon/hoadonManagerDetailView";
	}

	@GetMapping("/admin/hoadon/print/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
	public String printInvoice(@PathVariable("id") Integer id, Model model) {
		try {
			AtomicReference<Double> tempPrice = new AtomicReference<>(0.0);
			List<SanPham> listSanPham = new ArrayList<>();
			HoaDon hoaDon = hoaDonService.getHoaDonById(Integer.valueOf(id));

			if (hoaDon != null && hoaDon.getHoaDonChiTiets() != null) {
				hoaDon.getHoaDonChiTiets().forEach(item -> {
					SanPham sanPham = sanPhamService.getSanPhamById(item.getId().getIdSanpham());
					if (sanPham != null) {
						double price = (item.getGiamgia() == 0) ? item.getGia() * item.getSoluong()
								: (item.getGia() * (100 - item.getGiamgia()) / 100) * item.getSoluong();
						tempPrice.updateAndGet(v -> v + price);
						sanPham.setSoluong(item.getSoluong());
						sanPham.setGia(item.getGia());
						sanPham.setGiamgia(item.getGiamgia());
						listSanPham.add(sanPham);
					}
				});
			}

			model.addAttribute("tempPrice", tempPrice.get());
			model.addAttribute("listSanPham", listSanPham);
			model.addAttribute("deliveryPrice",
					hoaDon != null && hoaDon.getGiaohang() != null && hoaDon.getGiaohang().contains("nhanh")
							? 50000
							: 20000);
			model.addAttribute("order", hoaDon);
		} catch (Exception e) {
			// Set default values to prevent template errors
			model.addAttribute("tempPrice", 0.0);
			model.addAttribute("listSanPham", new ArrayList<>());
			model.addAttribute("deliveryPrice", 20000);
			model.addAttribute("order", null);
			model.addAttribute("errorMessage", e.getMessage());
		}
		return "admin/hoadon/hoadonPrintView";
	}

	@PostMapping("/admin/hoadon/update")
	@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
	public String updateUser(RedirectAttributes redirectAttributes, @RequestParam("id") Integer id,
			@RequestParam("action") String action) {
		try {
			redirectAttributes.addFlashAttribute("successMessage", hoaDonService.updateHoadon(id, action));
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/hoadon";
	}

}
