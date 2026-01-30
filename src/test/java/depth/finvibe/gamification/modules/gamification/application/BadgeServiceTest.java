package depth.finvibe.gamification.modules.gamification.application;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.out.BadgeOwnershipRepository;
import depth.finvibe.gamification.modules.gamification.domain.error.GamificationErrorCode;
import depth.finvibe.gamification.modules.gamification.domain.enums.Badge;
import depth.finvibe.gamification.shared.error.DomainException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("BadgeService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private BadgeOwnershipRepository badgeOwnershipRepository;

    @InjectMocks
    private BadgeService badgeService;

    @Test
    @DisplayName("배지가 없으면 정상적으로 저장한다")
    void grant_badge_to_user_saves() {
        UUID userId = UUID.randomUUID();
        when(badgeOwnershipRepository.isExist(any())).thenReturn(false);

        badgeService.grantBadgeToUser(userId, Badge.FIRST_PROFIT);

        verify(badgeOwnershipRepository).save(any());
    }

    @Test
    @DisplayName("이미 보유한 배지는 예외가 발생한다")
    void grant_badge_to_user_throws_when_exists() {
        UUID userId = UUID.randomUUID();
        when(badgeOwnershipRepository.isExist(any())).thenReturn(true);

        assertThatThrownBy(() -> badgeService.grantBadgeToUser(userId, Badge.FIRST_PROFIT))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo(GamificationErrorCode.BADGE_ALREADY_EXIST);
        verify(badgeOwnershipRepository, never()).save(any());
    }
}
