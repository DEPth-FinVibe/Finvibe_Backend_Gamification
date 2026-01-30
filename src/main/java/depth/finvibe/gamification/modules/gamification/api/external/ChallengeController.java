package depth.finvibe.gamification.modules.gamification.api.external;

import java.util.List;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import depth.finvibe.gamification.boot.security.model.AuthenticatedUser;
import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.gamification.application.port.in.ChallengeQueryUseCase;
import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;

@Tag(name = "챌린지", description = "개인 챌린지 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeQueryUseCase challengeQueryUseCase;

    @Operation(summary = "내 챌린지 목록 조회", description = "인증된 사용자의 개인 챌린지 목록을 반환합니다")
    @GetMapping("/me")
    public List<ChallengeDto.ChallengeResponse> getMyChallenges(@AuthenticatedUser Requester requester) {
        return challengeQueryUseCase.getPersonalChallenges(requester.getUuid());
    }
}
