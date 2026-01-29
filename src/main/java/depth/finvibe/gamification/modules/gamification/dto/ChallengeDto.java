package depth.finvibe.gamification.modules.gamification.dto;

import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChallengeDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ChallengeGenerationResponse {
        private String title;

        private String description;

        private UserMetricType metricType;

        private Double targetValue;

        private Long rewardXp;
    }
}
