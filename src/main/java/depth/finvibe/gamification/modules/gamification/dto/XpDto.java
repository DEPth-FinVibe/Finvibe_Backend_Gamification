package depth.finvibe.gamification.modules.gamification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class XpDto {

    @Getter
    @Builder
    public static class Response {
        private UUID userId;
        private Long totalXp;
        private Integer level;
    }
}
