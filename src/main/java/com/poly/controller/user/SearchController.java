package com.poly.controller.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.entity.SanPham;
import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;

@Controller
public class SearchController {
	@Autowired
	SanPhamService sanPhamService;
	@Autowired
	LoaiService loaiService;
	private static final int PRODUCTS_PER_PAGE = 12;

	@GetMapping("/search")
	public String search(Model model,
			@RequestParam(name = "q", required = false) String q,
			@RequestParam(defaultValue = "0", name = "page") int page,
			@RequestParam(name = "idLoai", required = false) Integer idLoai,
			@RequestParam(name = "priceRanges", required = false) String priceRanges,
			@RequestParam(name = "order", defaultValue = "createdAt-DESC", required = false) String order) {
		Optional<String> query = Optional.ofNullable(q).filter(s -> !s.trim().isEmpty());
		if (query.isPresent()) {
			String queryStr = query.get();
			Page<SanPham> sanPhamPage;
			if (idLoai != null) {
				sanPhamPage = sanPhamService.searchByTenAndLoai(page, PRODUCTS_PER_PAGE, queryStr, idLoai);
			} else if ((priceRanges != null && !priceRanges.isEmpty()) || !"createdAt-DESC".equals(order)) {
				sanPhamPage = sanPhamService.searchWithFilter(page, PRODUCTS_PER_PAGE, queryStr, priceRanges, order);
			} else {
				sanPhamPage = sanPhamService.searchByName(page, PRODUCTS_PER_PAGE, queryStr);
			}
			model.addAttribute("sanphams", sanPhamPage.getContent());
			model.addAttribute("totalProducts", sanPhamPage.getTotalElements());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", sanPhamPage.getTotalPages());
			model.addAttribute("query", queryStr);
			model.addAttribute("idLoai", idLoai);
			model.addAttribute("priceRanges", priceRanges);
			model.addAttribute("order", order);
			model.addAttribute("loais", loaiService.getAllLoai(0, 100));
			model.addAttribute("allLoais", loaiService.getAllLoai(0, 100));
			return "user/search";
		} else {
			return "redirect:/";
		}
	}
}
