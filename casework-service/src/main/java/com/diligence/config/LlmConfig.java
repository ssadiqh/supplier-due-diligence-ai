package com.diligence.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI configuration for LLM integration (Phase 5.5).
 *
 * When Spring AI is available in Maven:
 * 1. Uncomment spring-ai-openai-spring-boot-starter in pom.xml
 * 2. Uncomment the ChatClient bean below
 * 3. DocumentEvidenceAgent will automatically receive the ChatClient and enable LLM extraction
 *
 * Without Spring AI dependency, DocumentEvidenceAgent gracefully falls back to NOT_CONFIGURED status.
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.openai.api-key")
public class LlmConfig {

    // Spring AI ChatClient bean configuration
    // Uncomment when spring-ai-openai-spring-boot-starter is available in Maven:
    /*
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
    */
}
