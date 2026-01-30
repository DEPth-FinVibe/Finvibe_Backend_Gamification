package depth.finvibe.gamification.modules.gamification.application;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.out.BadgeOwnershipRepository;
import depth.finvibe.gamification.modules.gamification.domain.BadgeOwnership;
import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;
import depth.finvibe.gamification.modules.gamification.dto.BadgeDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("BadgeQueryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class BadgeQueryServiceTest {

    @Mock
    private BadgeOwnershipRepository badgeOwnershipRepository;

    @InjectMocks
    private BadgeQueryService badgeQueryService;

    @Test
    @DisplayName("유저 배지 조회 시 배지 정보를 반환한다")
    void get_user_badges_returns_info() {
        UUID userId = UUID.randomUUID();
        BadgeOwnership ownership = BadgeOwnership.of(Badge.FIRST_PROFIT, userId);

        when(badgeOwnershipRepository.findByUserId(userId)).thenReturn(List.of(ownership));

        List<BadgeDto.BadgeInfo> result = badgeQueryService.getUserBadges(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBadge()).isEqualTo(Badge.FIRST_PROFIT);
        assertThat(result.get(0).getDisplayName()).isEqualTo(Badge.FIRST_PROFIT.getDisplayName());
    }

    @Test
    @DisplayName("전체 배지 조회 시 모든 배지 정보를 반환한다")
    void get_all_badges_returns_all() {
        List<BadgeDto.BadgeStatistics> result = badgeQueryService.getAllBadges();

        assertThat(result).hasSize(Badge.getAllBadges().size());
        assertThat(result.get(0).getDisplayName()).isEqualTo(result.get(0).getBadge().getDisplayName());
    }
}
