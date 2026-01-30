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
import depth.finvibe.gamification.modules.gamification.application.port.in.XpQueryUseCase;
import depth.finvibe.gamification.modules.gamification.dto.XpDto;

@Tag(name = "경험치", description = "경험치 및 랭킹 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/xp")
public class XpController {

    private final XpQueryUseCase xpQueryUseCase;

    @Operation(summary = "내 경험치 조회", description = "인증된 사용자의 경험치 정보를 반환합니다")
    @GetMapping("/me")
    public XpDto.Response getMyXp(@AuthenticatedUser Requester requester) {
        return xpQueryUseCase.getUserXp(requester.getUuid());
    }

    @Operation(summary = "스쿼드 경험치 랭킹 조회", description = "스쿼드별 경험치 랭킹을 조회합니다")
    @GetMapping("/squads/ranking")
    public List<XpDto.SquadRankingResponse> getSquadXpRanking() {
        return xpQueryUseCase.getSquadXpRanking();
    }

    @Operation(summary = "내 스쿼드 기여도 랭킹 조회", description = "사용자 소속 스쿼드의 기여도 랭킹을 조회합니다")
    @GetMapping("/squads/contributions/me")
    public List<XpDto.ContributionRankingResponse> getMySquadContributionRanking(
            @AuthenticatedUser Requester requester) {
        return xpQueryUseCase.getSquadContributionRanking(requester.getUuid());
    }
}
