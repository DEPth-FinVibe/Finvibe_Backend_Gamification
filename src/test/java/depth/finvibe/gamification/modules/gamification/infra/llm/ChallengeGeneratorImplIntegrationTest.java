package depth.finvibe.gamification.modules.gamification.infra.llm;

import java.util.List;

import depth.finvibe.gamification.config.TestRedissonConfig;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import depth.finvibe.gamification.modules.gamification.application.port.out.ChallengeGenerator;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("llm-test")
@Import(TestRedissonConfig.class)
class ChallengeGeneratorImplIntegrationTest {

    @Autowired
    private ChallengeGenerator challengeGenerator;

    @Test
    @DisplayName("외부 LLM 연동으로 챌린지 생성 성공")
    void generate_success_with_external_llm() {
        String apiKey = System.getenv("GEMINI_API_KEY");
        Assumptions.assumeTrue(StringUtils.hasText(apiKey), "GEMINI_API_KEY not set");

        List<ChallengeDto.ChallengeGenerationResponse> result = challengeGenerator.generate();

        assertThat(result).hasSize(3);
        assertThat(result).allSatisfy(item -> {
            assertThat(item.getTitle()).isNotBlank();
            assertThat(item.getDescription()).isNotBlank();
            assertThat(item.getMetricType()).isNotNull();
            assertThat(item.getTargetValue()).isNotNull();
            assertThat(item.getTargetValue()).isGreaterThan(0);
            assertThat(item.getRewardXp()).isNotNull();
            assertThat(item.getRewardXp()).isGreaterThan(0L);
        });
    }
}
