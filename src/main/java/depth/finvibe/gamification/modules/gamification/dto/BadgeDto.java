package depth.finvibe.gamification.modules.gamification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BadgeDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class BadgeInfo {
        private Badge badge;
        private String displayName;
        private LocalDateTime acquiredAt;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class BadgeStatistics {
        private Badge badge;
        private String displayName;
    }
}