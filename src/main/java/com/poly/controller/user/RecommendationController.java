package com.poly.controller.user;

import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    private static final Set<String> KNOWN_INGREDIENTS = new LinkedHashSet<>(List.of(
            "paracetamol",
            "ibuprofen",
            "cetirizine",
            "loratadine",
            "amoxicillin",
            "diclofenac",
            "omeprazole",
            "vitamin c",
            "kẽm",
            "zinc",
            "dha"));

    // Gợi ý sản phẩm chăm sóc đi kèm (upsell) dựa trên sản phẩm đã xem / chi tiết
    @GetMapping("/upsell/{productId}")
    public ResponseEntity<?> upsell(@PathVariable("productId") Integer productId,
                                    @RequestParam(defaultValue = "5") int limit) {
        List<SanPham> list = sanPhamRepository
                .recommendByCoPurchaseSameCategory(productId, PageRequest.of(0, Math.min(limit, 10)))
                .getContent();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/ingredient/{productId}")
    public ResponseEntity<?> ingredientBased(@PathVariable("productId") Integer productId,
                                             @RequestParam(defaultValue = "6") int limit) {
        Optional<SanPham> optionalBaseProduct = sanPhamRepository.findById(productId);
        if (optionalBaseProduct.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        SanPham base = optionalBaseProduct.get();
        String ingredient = detectIngredient(base);

        List<SanPham> list;
        if (ingredient != null) {
            list = sanPhamRepository.findByIngredientKeyword(ingredient, productId, PageRequest.of(0, Math.min(limit, 10)));
            if (list.isEmpty()) {
                // fallback cùng loại nếu chưa có dữ liệu thành phần
                list = sanPhamRepository
                        .recommendByCoPurchaseSameCategory(productId, PageRequest.of(0, Math.min(limit, 10)))
                        .getContent();
            }
        } else {
            // fallback cùng loại nếu không detect được hoạt chất
            list = sanPhamRepository
                    .recommendByCoPurchaseSameCategory(productId, PageRequest.of(0, Math.min(limit, 10)))
                    .getContent();
        }

        String reason = ingredient != null
                ? "Gợi ý cùng hoạt chất: " + ingredient
                : "Gợi ý sản phẩm liên quan";

        List<Map<String, Object>> response = list.stream().map(sp -> {
            Map<String, Object> item = new HashMap<>();
            item.put("idSanpham", sp.getIdSanpham());
            item.put("tenSanpham", sp.getTenSanpham());
            item.put("gia", sp.getGia());
            item.put("giamgia", sp.getGiamgia());
            item.put("hinh", sp.getHinh());
            item.put("reason", reason);
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private String detectIngredient(SanPham product) {
        String text = String.join(" ",
                product.getTenSanpham() == null ? "" : product.getTenSanpham(),
                product.getMota() == null ? "" : product.getMota(),
                product.getMotangan() == null ? "" : product.getMotangan()).toLowerCase();

        for (String ingredient : KNOWN_INGREDIENTS) {
            if (text.contains(ingredient.toLowerCase())) {
                return ingredient;
            }
        }
        return null;
    }
}
