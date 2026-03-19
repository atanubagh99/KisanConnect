package com.kisanconnect.advisory;

import com.kisanconnect.advisory.dto.FarmerQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Advisory generation service using RAG (Retrieval Augmented Generation).
 * Optimized for Groq free tier (6000 TPM limit).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdvisoryService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final String SYSTEM_PROMPT = "You are KisanConnect agriculture advisor. Give concise advice in %s. Max 100 words.";

    // Max characters for RAG context (~400 tokens ≈ 1500 chars)
    private static final int MAX_CONTEXT_CHARS = 1500;

    /**
     * Generate an agricultural advisory for the farmer's query.
     */
    @Cacheable(value = "advisories", key = "#query.text().toLowerCase().trim()", unless = "#result == null || #result.isEmpty()")
    public String generateAdvisory(FarmerQuery query) {
        log.info("Generating advisory for: '{}' (lang: {})",
                truncate(query.text(), 60), query.language());

        // Step 1: Retrieve top-2 relevant documents (saves tokens)
        String context = retrieveContext(query.text());

        // Step 2: Build compact prompt
        String languageName = mapLanguageCode(query.language());
        String systemPrompt = String.format(SYSTEM_PROMPT, languageName);
        String userPrompt = buildUserPrompt(query, context);

        log.info("Prompt size: system={}chars, user={}chars",
                systemPrompt.length(), userPrompt.length());

        // Step 3: Generate advisory via LLM
        try {
            String advisory = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            log.info("Advisory generated ({} chars)", advisory.length());
            return advisory;
        } catch (Exception e) {
            log.error("LLM generation failed: {}", e.getMessage(), e);
            return getFallbackAdvisory(query.language());
        }
    }

    /**
     * Retrieve top-2 relevant docs, truncated to MAX_CONTEXT_CHARS.
     */
    private String retrieveContext(String queryText) {
        try {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(queryText)
                            .topK(1) // Only top 1 doc to stay under 6000 TPM
                            .similarityThreshold(0.7)
                            .build());

            if (docs.isEmpty()) {
                log.info("No relevant documents found");
                return "";
            }

            // Use getText() NOT getFormattedContent() to avoid metadata bloat
            String context = docs.getFirst().getText();

            // Truncate aggressively
            if (context.length() > MAX_CONTEXT_CHARS) {
                context = context.substring(0, MAX_CONTEXT_CHARS);
            }

            log.info("Context: {} chars from {} doc(s)", context.length(), docs.size());
            return context;
        } catch (Exception e) {
            log.warn("Vector store search failed", e);
            return "Database temporarily unavailable.";
        }
    }

    private String buildUserPrompt(FarmerQuery query, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("CONTEXT:\n").append(context).append("\n\n");
        prompt.append("QUESTION: ").append(query.text());
        return prompt.toString();
    }

    private String mapLanguageCode(String code) {
        return switch (code) {
            case "hi" -> "Hindi";
            case "bn" -> "Bengali";
            case "te" -> "Telugu";
            case "ta" -> "Tamil";
            case "kn" -> "Kannada";
            case "ml" -> "Malayalam";
            case "mr" -> "Marathi";
            case "gu" -> "Gujarati";
            case "or" -> "Odia";
            case "pa" -> "Punjabi";
            case "ur" -> "Urdu";
            default -> "English";
        };
    }

    private String getFallbackAdvisory(String language) {
        return switch (language) {
            case "hi" -> "क्षमा करें, AI सेवा अभी उपलब्ध नहीं है। कृपया अपने KVK या कृषि अधिकारी से संपर्क करें।";
            case "bn" -> "দুঃখিত, AI সেবা এখন উপলব্ধ নয়। আপনার নিকটতম KVK-এ যোগাযোগ করুন।";
            default ->
                "Sorry, the AI service is temporarily unavailable. Please contact your nearest KVK or agriculture officer.";
        };
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
