package com.poly.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GeminiService {

    private final String apiKey;
    private final String modelId;
    private final String systemPrompt;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public GeminiService(
            @Value("${GEMINI_API_KEY:${gemini.api-key:}}") String apiKey,
            @Value("${GEMINI_MODEL_ID:${gemini.model-id:gemini-1.5-flash}}") String modelId,
            @Value("${GEMINI_SYSTEM_PROMPT:${gemini.system-prompt:}}") String systemPrompt) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.modelId = (modelId == null || modelId.isBlank()) ? "gemini-2.0-flash" : modelId.trim();
        this.systemPrompt = (systemPrompt == null || systemPrompt.isBlank()) ? defaultSystemPrompt()
                : systemPrompt.trim();
        this.httpClient = HttpClient.newHttpClient();
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String ask(String question) throws Exception {
        if (!isConfigured()) {
            throw new IllegalStateException("GEMINI_API_KEY is not configured");
        }

        ObjectNode root = mapper.createObjectNode();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            ObjectNode sys = root.putObject("systemInstruction");
            ArrayNode sysParts = sys.putArray("parts");

            // Lấy thời gian hiện tại của hệ thống để AI không bị "mù" thời gian
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("EEEE, dd/MM/yyyy HH:mm:ss", new java.util.Locale("vi", "VN"));
            String currentTime = now.format(formatter);

            String fullPrompt = systemPrompt + "\n\nThông tin hệ thống:\n- Thời gian hiện tại của bạn: " + currentTime;
            sysParts.addObject().put("text", fullPrompt);
        }
        ArrayNode contents = root.putArray("contents");
        ObjectNode userMsg = contents.addObject();
        userMsg.put("role", "user");
        ArrayNode parts = userMsg.putArray("parts");
        parts.addObject().put("text", question);

        ObjectNode generationConfig = root.putObject("generationConfig");
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 2048);

        String body = mapper.writeValueAsString(root);
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelId + ":generateContent?key="
                + apiKey;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("Gemini API error: HTTP " + res.statusCode() + " - " + res.body());
        }
        return extractText(res.body());
    }

    private String defaultSystemPrompt() {
        return String.join("\n",
                "Bạn là trợ lý tư vấn sức khỏe và dược phẩm của nhà thuốc MEDISALE.",
                "",
                "NHIỆM VỤ CHÍNH:",
                "- Hỗ trợ các câu hỏi liên quan đến sức khỏe, triệu chứng bệnh, thuốc và dược phẩm.",
                "- Khi người dùng mô tả triệu chứng, hãy phân tích và gợi ý các loại thuốc phù hợp mà nhà thuốc có thể cung cấp (ví dụ: thuốc hạ sốt, thuốc cảm cúm, vitamin, kháng sinh OTC, thuốc tiêu hóa, v.v.).",
                "- Cung cấp thông tin về cách dùng thuốc, liều lượng thông thường, tác dụng phụ cần lưu ý.",
                "- Tư vấn về dinh dưỡng, lối sống lành mạnh liên quan đến sức khỏe.",
                "",
                "QUY TẮC BẮT BUỘC:",
                "- Luôn trả lời bằng tiếng Việt, thân thiện và dễ hiểu.",
                "- Trả lời ngắn gọn, có cấu trúc rõ ràng (dùng gạch đầu dòng khi cần liệt kê).",
                "- Nếu câu hỏi KHÔNG liên quan đến sức khỏe, thuốc, y tế hoặc nhà thuốc, hãy lịch sự từ chối và hướng người dùng hỏi về sức khỏe.",
                "- TUYỆT ĐỐI KHÔNG tự ý kê đơn thuốc kê đơn (prescription drugs) — chỉ tư vấn thuốc OTC và thực phẩm chức năng.",
                "- Với các trường hợp cấp cứu hoặc triệu chứng nghiêm trọng (đau ngực, khó thở, đột quỵ...), hãy yêu cầu người dùng GỌI 115 NGAY.",
                "",
                "DISCLAIMER BẮT BUỘC (thêm vào CUỐI MỖI câu trả lời):",
                "⚠️ *Đây chỉ là tư vấn của AI. Bạn nên gặp dược sĩ hoặc bác sĩ để được chẩn đoán và điều trị chính xác nhất.*");
    }

    private String extractText(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode first = candidates.get(0);
            JsonNode parts = first.path("content").path("parts");
            if (parts.isArray() && parts.size() > 0) {
                JsonNode text = parts.get(0).path("text");
                if (text.isTextual())
                    return text.asText();
            }
        }
        return "Xin lỗi, mình chưa có câu trả lời phù hợp.";
    }
}
