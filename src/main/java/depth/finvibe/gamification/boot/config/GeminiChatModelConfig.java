package depth.finvibe.gamification.boot.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class GeminiChatModelConfig {

    @Bean
    public ChatModel geminiChatModel(GeminiChatModelProperties properties) {
        GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder builder = GoogleAiGeminiChatModel.builder()
            .apiKey(properties.apiKey())
            .modelName(properties.modelName());

        if (properties.timeout() != null) {
            builder.timeout(properties.timeout());
        }

        if (properties.temperature() != null) {
            builder.temperature(properties.temperature());
        }

        return builder.build();
    }
}
