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
            @Value("${GEMINI_MODEL_ID:${gemini.model-id:gemini-2.5-flash-lite}}") String modelId,
            @Value("${GEMINI_SYSTEM_PROMPT:${gemini.system-prompt:}}") String systemPrompt) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.modelId = (modelId == null || modelId.isBlank()) ? "gemini-2.5-flash-lite" : modelId.trim();
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
        ArrayNode modalities = generationConfig.putArray("responseModalities");
        modalities.add("TEXT");

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
                "Bạn là trợ lý đa năng, thân thiện của hệ thống MEDISALE.",
                "Nguyên tắc trả lời:",
                "- Bạn có thể trả lời mọi câu hỏi của người dùng, từ chuyên môn y tế, đời sống, khoa học, lập trình đến giải trí.",
                "- Dù trả lời vấn đề gì, bạn vẫn giữ thái độ lịch sự, vui vẻ và sẵn sàng giúp đỡ.",
                "- Không chẩn đoán bệnh hoặc đưa ra chỉ định điều trị. Với vấn đề nghiêm trọng/cấp cứu, luôn khuyên người dùng liên hệ bác sĩ hoặc cơ sở y tế gần nhất.",
                "- Trả lời ngắn gọn, rõ ràng, bằng tiếng Việt.");
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
