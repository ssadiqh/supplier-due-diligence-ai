package com.diligence.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI configuration for LLM integration with Claude via Anthropic.
 * ChatModel is auto-configured when spring.ai.anthropic.api-key is configured.
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.anthropic.api-key")
public class LlmConfig {
    // ChatModel bean is auto-configured by Spring AI auto-configuration when Anthropic is on the classpath
}
