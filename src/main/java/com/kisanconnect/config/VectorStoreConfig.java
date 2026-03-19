package com.kisanconnect.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Vector store configuration.
 * Uses SimpleVectorStore (in-memory) for all profiles.
 * For production, swap to pgvector by adding the pgvector starter dependency.
 */
@Slf4j
@Configuration
public class VectorStoreConfig {

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        log.info("📦 Initializing SimpleVectorStore (in-memory)");
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
