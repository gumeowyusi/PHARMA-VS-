package com.poly.controller.user;

import com.poly.entity.LichSuDiem;
import com.poly.service.CurrentUserService;
import com.poly.service.DiemTichLuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diem")
public class DiemController {

    @Autowired
    private DiemTichLuyService diemTichLuyService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping("/lich-su")
    public ResponseEntity<?> lichSu() {
        return currentUserService.getCurrentUser().map(u -> {
            List<LichSuDiem> list = diemTichLuyService.getLichSuByUser(u.getIdUser());
            return ResponseEntity.ok(list);
        }).orElse(ResponseEntity.status(401).body(List.of()));
    }

    @GetMapping("/tong")
    public ResponseEntity<?> tongDiem() {
        return currentUserService.getCurrentUser().map(u ->
            ResponseEntity.ok(Map.of("diem", u.getDiemTichLuy() != null ? u.getDiemTichLuy() : 0))
        ).orElse(ResponseEntity.status(401).body(Map.of("diem", 0)));
    }
}
