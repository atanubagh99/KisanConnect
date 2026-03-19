package com.kisanconnect.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Diagnostic endpoint to test individual pipeline components.
 * Available at /api/diagnostics
 */
@Slf4j
@RestController
@RequestMapping("/api/diagnostics")
@RequiredArgsConstructor
public class DiagnosticsController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    /**
     * Test the Groq LLM connection.
     * GET /api/diagnostics/llm?prompt=Hello
     */
    @GetMapping("/llm")
    public ResponseEntity<Map<String, Object>> testLlm(
            @RequestParam(defaultValue = "Say hello in one sentence") String prompt) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("test", "Groq LLM");
        result.put("prompt", prompt);

        try {
            long start = System.currentTimeMillis();
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            long elapsed = System.currentTimeMillis() - start;

            result.put("status", "SUCCESS");
            result.put("response", response);
            result.put("latencyMs", elapsed);
            log.info("✅ LLM test passed: {} ({}ms)", response, elapsed);
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                result.put("rootCause", e.getCause().getMessage());
            }
            log.error("❌ LLM test failed", e);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Test the vector store search.
     * GET /api/diagnostics/vectorstore?query=rice pest
     */
    @GetMapping("/vectorstore")
    public ResponseEntity<Map<String, Object>> testVectorStore(
            @RequestParam(defaultValue = "rice pest control") String query) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("test", "Vector Store Search");
        result.put("query", query);

        try {
            long start = System.currentTimeMillis();
            var docs = vectorStore.similaritySearch(query);
            long elapsed = System.currentTimeMillis() - start;

            result.put("status", "SUCCESS");
            result.put("documentsFound", docs.size());
            result.put("latencyMs", elapsed);
            if (!docs.isEmpty()) {
                result.put("topResult", docs.getFirst().getFormattedContent().substring(0,
                        Math.min(200, docs.getFirst().getFormattedContent().length())));
            }
            log.info("✅ Vector store test: found {} docs ({}ms)", docs.size(), elapsed);
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                result.put("rootCause", e.getCause().getMessage());
            }
            log.error("❌ Vector store test failed", e);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Full pipeline health check.
     * GET /api/diagnostics/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> pipelineHealth() {
        Map<String, String> health = new LinkedHashMap<>();

        // Test LLM
        try {
            chatClient.prompt().user("reply with OK").call().content();
            health.put("llm", "✅ OK");
        } catch (Exception e) {
            health.put("llm", "❌ " + e.getMessage());
        }

        // Test Vector Store
        try {
            vectorStore.similaritySearch("test");
            health.put("vectorStore", "✅ OK");
        } catch (Exception e) {
            health.put("vectorStore", "❌ " + e.getMessage());
        }

        return ResponseEntity.ok(health);
    }
}
