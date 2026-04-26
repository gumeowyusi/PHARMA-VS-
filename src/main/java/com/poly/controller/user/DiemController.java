package com.poly.controller.user;

import com.poly.entity.LichSuDiem;
import com.poly.entity.Voucher;
import com.poly.service.CurrentUserService;
import com.poly.service.DiemTichLuyService;
import com.poly.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diem")
public class DiemController {

    @Autowired
    private DiemTichLuyService diemTichLuyService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private VoucherService voucherService;

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

    /**
     * Exchange points for a fixed-value voucher.
     * Rule: 10,000 points = 10,000 VND voucher, multiples of 10,000 only.
     */
    @PostMapping("/doi-voucher")
    public ResponseEntity<?> doiVoucher(@RequestBody Map<String, Integer> body) {
        int points = body.getOrDefault("points", 0);
        if (points <= 0 || points % 10000 != 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Số điểm phải là bội số của 10.000"));
        }
        return currentUserService.getCurrentUser().map(u -> {
            int available = u.getDiemTichLuy() == null ? 0 : u.getDiemTichLuy();
            if (available < points) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Không đủ điểm. Bạn có " + available + " điểm."));
            }
            boolean deducted = diemTichLuyService.redeemPoints(
                    u.getIdUser(), points,
                    "Đổi " + points + " điểm lấy voucher " + points + "₫");
            if (!deducted) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không thể trừ điểm, vui lòng thử lại"));
            }
            // Create a personal voucher valid 30 days, 1 usage
            String code = "DIEM" + u.getIdUser().replaceAll("[^A-Za-z0-9]", "").toUpperCase().substring(0, Math.min(6, u.getIdUser().length())) + System.currentTimeMillis() % 100000;
            Voucher v = new Voucher();
            v.setCode(code);
            v.setType("FIXED");
            v.setValue((double) points);        // 1 point = 1 VND
            v.setMinOrderValue(0.0);
            v.setUsageLimit(1);
            v.setUsagePerUser(1);
            v.setTotalUsed(0);
            v.setActive(true);
            v.setStartDate(new Date());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 30);
            v.setEndDate(cal.getTime());
            voucherService.create(v);
            return ResponseEntity.ok(Map.of(
                    "message", "Đổi điểm thành công!",
                    "code", code,
                    "value", points));
        }).orElse(ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập")));
    }
}
