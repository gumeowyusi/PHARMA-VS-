package com.poly.controller.user;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.poly.entity.HoaDon;
import com.poly.entity.HoaDonChiTiet;
import com.poly.service.HoaDonService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private static final String PAYOS_API = "https://api-merchant.payos.vn/v2/payment-requests";

    @Value("${PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${PAYOS_API_KEY}")
    private String apiKey;

    @Value("${PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    private final HoaDonService hoaDonService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public PaymentController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    record CreateBankTransferRequest(int orderId, double deliveryPrice) {}

    /** Tính HMAC-SHA256 signature cho PayOS request */
    private String hmacSha256(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @ResponseBody
    @PostMapping("/api/payments/bank-transfer")
    public ResponseEntity<?> createBankTransfer(@RequestBody CreateBankTransferRequest req,
                                                 HttpServletRequest request) {
        try {
            HoaDon hoaDon = hoaDonService.getHoaDonById(req.orderId());
            if (hoaDon == null)
                return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy đơn hàng"));

            List<HoaDonChiTiet> items = hoaDon.getHoaDonChiTiets();
            if (items == null || items.isEmpty())
                return ResponseEntity.status(400).body(Map.of("message", "Đơn hàng trống"));

            // Tính tổng tiền từ DB
            int tempTotal = items.stream().mapToInt(ct ->
                ct.getGia() * (100 - ct.getGiamgia()) / 100 * ct.getSoluong()
            ).sum();
            int amount = (int) Math.max(1000, Math.round(tempTotal + req.deliveryPrice()));

            // Tạo orderCode từ timestamp
            String ts = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(ts.substring(Math.max(0, ts.length() - 10)));

            // Base URL
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort())
                    + request.getContextPath();

            // Truyền orderId vào returnUrl để server xử lý sau khi thanh toán
            String returnUrl = baseUrl + "/cart?pay=success&code=" + orderCode + "&orderId=" + req.orderId();
            String cancelUrl = baseUrl + "/cart?pay=cancel&code=" + orderCode + "&orderId=" + req.orderId();

            // Description: chỉ A-Z, 0-9, space; max 25 ký tự
            String desc = "THANH TOAN " + orderCode;
            if (desc.length() > 25) desc = "DH " + orderCode;
            if (desc.length() > 25) desc = String.valueOf(orderCode).substring(0, Math.min(25, String.valueOf(orderCode).length()));

            // Tính signature
            String sigData = "amount=" + amount
                    + "&cancelUrl=" + cancelUrl
                    + "&description=" + desc
                    + "&orderCode=" + orderCode
                    + "&returnUrl=" + returnUrl;
            String signature = hmacSha256(sigData);

            // Build JSON body
            ObjectNode body = objectMapper.createObjectNode();
            body.put("orderCode", orderCode);
            body.put("amount", amount);
            body.put("description", desc);
            body.put("returnUrl", returnUrl);
            body.put("cancelUrl", cancelUrl);
            body.put("signature", signature);

            ArrayNode itemsNode = body.putArray("items");
            for (HoaDonChiTiet ct : items) {
                ObjectNode item = objectMapper.createObjectNode();
                item.put("name", ct.getSanPham().getTenSanpham());
                item.put("quantity", ct.getSoluong());
                item.put("price", ct.getGia() * (100 - ct.getGiamgia()) / 100);
                itemsNode.add(item);
            }

            String bodyStr = objectMapper.writeValueAsString(body);
            log.info("[PayOS] POST {} | orderCode={} amount={} desc='{}'", PAYOS_API, orderCode, amount, desc);

            // Gọi PayOS HTTP API trực tiếp
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(PAYOS_API))
                    .header("Content-Type", "application/json")
                    .header("x-client-id", clientId)
                    .header("x-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(bodyStr, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> httpResp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
            log.info("[PayOS] Response {}: {}", httpResp.statusCode(), httpResp.body());

            JsonNode respNode = objectMapper.readTree(httpResp.body());
            String code = respNode.path("code").asText();

            if (!"00".equals(code)) {
                String errDesc = respNode.path("desc").asText("Lỗi không xác định từ PayOS");
                log.error("[PayOS] Error code={} desc={}", code, errDesc);
                return ResponseEntity.status(400).body(Map.of("message", "PayOS lỗi: " + errDesc));
            }

            String checkoutUrl = respNode.path("data").path("checkoutUrl").asText();
            log.info("[PayOS] checkoutUrl={}", checkoutUrl);

            return ResponseEntity.ok(Map.of(
                    "orderCode", orderCode,
                    "checkoutUrl", checkoutUrl
            ));

        } catch (Exception e) {
            log.error("[PayOS] Exception: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi tạo link PayOS: " + e.getMessage()));
        }
    }

    @ResponseBody
    @GetMapping("/api/payments/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam("orderCode") long orderCode) {
        try {
            String url = "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode;
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-client-id", clientId)
                    .header("x-api-key", apiKey)
                    .GET()
                    .build();
            HttpResponse<String> httpResp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(httpResp.body());
            String status = node.path("data").path("status").asText("UNKNOWN");
            return ResponseEntity.ok(Map.of("status", status));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", "Không tìm thấy thông tin thanh toán"));
        }
    }
}
