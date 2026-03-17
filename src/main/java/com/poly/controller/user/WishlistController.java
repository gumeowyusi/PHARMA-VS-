package com.poly.controller.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.entity.Users;
import com.poly.entity.Wishlist;
import com.poly.service.CurrentUserService;
import com.poly.service.LoaiService;
import com.poly.service.WishlistService;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private LoaiService loaiService;

    @GetMapping
    public String viewWishlist(Model model,
            @RequestParam(defaultValue = "0") int page) {
        Optional<Users> userOpt = currentUserService.getCurrentUser();
        if (userOpt.isEmpty()) return "redirect:/signin";

        String idUser = userOpt.get().getIdUser();
        Page<Wishlist> wishlistPage = wishlistService.getWishlist(idUser, page, 12);
        model.addAttribute("wishlistPage", wishlistPage);
        model.addAttribute("wishlists", wishlistPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wishlistPage.getTotalPages());
        model.addAttribute("totalItems", wishlistPage.getTotalElements());
        model.addAttribute("loais", loaiService.getAllLoai(0, 100));
        return "user/wishlist";
    }

    @PostMapping("/toggle/{idSanpham}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Integer idSanpham) {
        Optional<Users> userOpt = currentUserService.getCurrentUser();
        Map<String, Object> res = new HashMap<>();
        if (userOpt.isEmpty()) {
            res.put("success", false);
            res.put("message", "Vui lòng đăng nhập");
            return ResponseEntity.status(401).body(res);
        }
        boolean added = wishlistService.toggle(userOpt.get().getIdUser(), idSanpham);
        res.put("success", true);
        res.put("added", added);
        res.put("message", added ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/remove/{idSanpham}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> remove(@PathVariable Integer idSanpham) {
        Optional<Users> userOpt = currentUserService.getCurrentUser();
        Map<String, Object> res = new HashMap<>();
        if (userOpt.isEmpty()) {
            res.put("success", false);
            return ResponseEntity.status(401).body(res);
        }
        wishlistService.remove(userOpt.get().getIdUser(), idSanpham);
        res.put("success", true);
        return ResponseEntity.ok(res);
    }
}
