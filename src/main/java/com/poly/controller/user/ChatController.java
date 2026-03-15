package com.poly.controller.user;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.service.GeminiService;

@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chatbot/ask")
    public ResponseEntity<Map<String, Object>> ask(@RequestBody Map<String, String> body) {
        // Giữ nguyên câu hỏi gốc (không lowercase) để Gemini hiểu đúng ngữ nghĩa
        String question = (body != null ? body.getOrDefault("question", "") : "").trim();
        String answer;

        if (question.isEmpty()) {
            answer = "Bạn muốn hỏi gì? Mình sẵn sàng hỗ trợ! 😊";
        } else if (!geminiService.isConfigured()) {
            log.warn("[Chatbot] Gemini API key chưa được cấu hình, dùng fallback.");
            answer = fallbackAnswer(question.toLowerCase());
        } else {
            try {
                log.info("[Chatbot] Gửi câu hỏi tới Gemini: {}", question);
                answer = geminiService.ask(question);
                log.info("[Chatbot] Gemini trả lời thành công.");
            } catch (Exception ex) {
                log.error("[Chatbot] Gemini API lỗi: {}", ex.getMessage(), ex);
                // Trả về thông báo lỗi thay vì fallback cứng
                answer = "Xin lỗi, trợ lý AI đang gặp sự cố kỹ thuật. Vui lòng thử lại sau. (Lỗi: " + ex.getMessage()
                        + ")";
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("answer", answer);
        res.put("ts", Instant.now().toString());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/support/session")
    public ResponseEntity<Map<String, Object>> handoff() {
        String ticket = "SUP-" + Long.toHexString(System.currentTimeMillis()).toUpperCase();
        Map<String, Object> res = new HashMap<>();
        res.put("ticketId", ticket);
        res.put("message", "Support ticket created");
        return ResponseEntity.ok(res);
    }

    private String fallbackAnswer(String q) {
        if (q.contains("giờ") || q.contains("thời gian") || q.contains("làm việc"))
            return "MEDISALE làm việc 8:00–21:00 (T2–CN).";
        if (q.contains("đổi trả") || q.contains("hoàn"))
            return "Đổi trả trong 7 ngày nếu còn nguyên tem và hoá đơn.";
        if (q.contains("giao hàng") || q.contains("ship") || q.contains("vận chuyển"))
            return "Giao nhanh 2H tại nội thành; tỉnh 2–5 ngày. Phí từ 20K.";
        if (q.contains("voucher") || q.contains("giảm giá") || q.contains("mã"))
            return "Xem mục Khuyến mãi hoặc nhập mã voucher ở giỏ hàng/checkout.";
        return "Bạn có thể mô tả cụ thể hơn không? Mình hỗ trợ về vận chuyển, đổi trả, voucher, giờ làm việc…";
    }
}
