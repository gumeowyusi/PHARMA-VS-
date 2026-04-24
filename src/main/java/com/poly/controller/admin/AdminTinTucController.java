package com.poly.controller.admin;

import com.poly.entity.TinTuc;
import com.poly.service.TinTucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

@Controller
@RequestMapping("/admin/tintuc")
public class AdminTinTucController {

    @Autowired
    private TinTucService tinTucService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<TinTuc> tinTucPage = tinTucService.getAllForAdmin(page, 10);
        model.addAttribute("tinTucs", tinTucPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tinTucPage.getTotalPages());
        return "admin/tintuc/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("tinTuc", new TinTuc());
        return "admin/tintuc/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute TinTuc tinTuc, RedirectAttributes ra) {
        try {
            if (tinTuc.getNgayDang() == null) tinTuc.setNgayDang(new Date());
            tinTucService.create(tinTuc);
            ra.addFlashAttribute("successMessage", "Đăng bài viết thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/tintuc";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("tinTuc", tinTucService.getById(id));
            return "admin/tintuc/edit";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/tintuc";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute TinTuc tinTuc, RedirectAttributes ra) {
        try {
            tinTucService.update(id, tinTuc);
            ra.addFlashAttribute("successMessage", "Cập nhật bài viết thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/tintuc";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            tinTucService.delete(id);
            ra.addFlashAttribute("successMessage", "Đã xóa bài viết!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/tintuc";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        tinTucService.toggleTrangThai(id);
        return "redirect:/admin/tintuc";
    }
}
