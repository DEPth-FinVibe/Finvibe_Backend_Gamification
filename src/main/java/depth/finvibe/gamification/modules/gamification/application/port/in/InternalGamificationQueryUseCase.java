package depth.finvibe.gamification.modules.gamification.application.port.in;

import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.dto.InternalGamificationDto;

public interface InternalGamificationQueryUseCase {

    InternalGamificationDto.UserSummaryResponse getUserSummary(UUID userId);
}
