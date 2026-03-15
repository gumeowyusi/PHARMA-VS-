package com.poly.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poly.service.LoaiService;
import com.poly.service.SanPhamService;

@Controller
public class QuickBuyController {

    private final SanPhamService sanPhamService;
    private final LoaiService loaiService;

    public QuickBuyController(SanPhamService sanPhamService, LoaiService loaiService) {
        this.sanPhamService = sanPhamService;
        this.loaiService = loaiService;
    }

    @GetMapping("/quick-buy")
    public String quickBuy(Model model) {
        model.addAttribute("loais", loaiService.getAllLoai(0, 5));
        model.addAttribute("sanphams", sanPhamService.getAllSanPham(0, 24).getContent());
        return "user/quickBuy";
    }
}
