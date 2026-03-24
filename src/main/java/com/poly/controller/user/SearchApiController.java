package com.poly.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.entity.SanPham;
import com.poly.repository.SanPhamRepository;

@RestController
@RequestMapping("/api/search")
public class SearchApiController {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @GetMapping("/suggestions")
    public ResponseEntity<?> suggestions(@RequestParam(name = "prefix") String prefix) {
        String trimmed = prefix == null ? "" : prefix.trim();
        if (trimmed.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Ưu tiên startsWith cho cảm giác "gõ chữ cái đầu"
        List<SanPham> items = sanPhamRepository.findTop10ByTenSanphamStartingWithIgnoreCase(trimmed);
        // Fallback contains để người dùng vẫn thấy gợi ý khi dữ liệu tên không bắt đầu đúng ký tự
        if (items.isEmpty()) {
            items = sanPhamRepository.findTop10ByTenSanphamContainingIgnoreCase(trimmed);
        }

        List<Map<String, Object>> res = items.stream()
                .map(sp -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("idSanpham", sp.getIdSanpham());
                    item.put("tenSanpham", sp.getTenSanpham());
                    return item;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }
}

