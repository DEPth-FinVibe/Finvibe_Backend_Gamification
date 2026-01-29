package depth.finvibe.gamification.modules.gamification.application.port.in;

import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;

import java.util.List;
import java.util.UUID;

public interface ChallengeQueryUseCase {
    /**
     * 사용자의 챌린지 목록을 조회합니다.
     * 진행 현황(Metric 값을 통해 산출)도 함께 포함됩니다.
     *
     * @param userId 사용자 ID
     * @return 챌린지 응답 목록
     */
    List<ChallengeDto.ChallengeResponse> getPersonalChallenges(UUID userId);
}
