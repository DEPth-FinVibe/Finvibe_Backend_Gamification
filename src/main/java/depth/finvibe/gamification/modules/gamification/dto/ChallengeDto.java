package depth.finvibe.gamification.modules.gamification.dto;

import depth.finvibe.gamification.modules.gamification.domain.enums.UserMetricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import depth.finvibe.gamification.modules.gamification.domain.PersonalChallenge;

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

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ChallengeResponse {
        private Long id;
        private String title;
        private String description;
        private UserMetricType metricType;
        private Double targetValue;
        private Double currentValue;
        private Double progressPercentage;
        private Long rewardXp;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isAchieved;

        public static ChallengeResponse from(PersonalChallenge challenge, Double currentValue) {
            Double targetValue = challenge.getCondition().getTargetValue();
            return ChallengeResponse.builder()
                    .id(challenge.getId())
                    .title(challenge.getTitle())
                    .description(challenge.getDescription())
                    .metricType(challenge.getCondition().getMetricType())
                    .targetValue(targetValue)
                    .currentValue(currentValue)
                    .progressPercentage(Math.min(100.0, (currentValue / targetValue) * 100.0))
                    .rewardXp(challenge.getReward().getRewardXp())
                    .startDate(challenge.getPeriod().getStartDate())
                    .endDate(challenge.getPeriod().getEndDate())
                    .isAchieved(currentValue >= targetValue)
                    .build();
        }
    }
}
