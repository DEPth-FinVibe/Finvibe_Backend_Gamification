package depth.finvibe.gamification.modules.gamification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class XpDto {

    @Getter
    @Builder
    public static class Response {
        private UUID userId;
        private Long totalXp;
        private Integer level;
    }

    @Getter
    @Builder
    public static class SquadRankingResponse {
        private Long squadId;
        private String squadName;
        private Integer currentRanking;
        private Long totalXp;
        private Long weeklyXp;
        private Double weeklyXpChangeRate;
        private Integer rankingChange; // +2, -1, 0 등
    }

    @Getter
    @Builder
    public static class ContributionRankingResponse {
        private String nickname;
        private Integer ranking;
        private Long weeklyContributionXp;
    }
}
