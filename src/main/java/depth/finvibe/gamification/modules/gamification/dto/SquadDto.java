package depth.finvibe.gamification.modules.gamification.dto;

import lombok.Builder;
import lombok.Getter;

public class SquadDto {

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String region;
    }
}
