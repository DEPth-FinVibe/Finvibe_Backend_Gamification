package depth.finvibe.gamification.boot.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "langchain4j.google-ai-gemini.chat-model")
public record GeminiChatModelProperties(
    String apiKey,
    String modelName,
    Duration timeout,
    Double temperature
) {
}
