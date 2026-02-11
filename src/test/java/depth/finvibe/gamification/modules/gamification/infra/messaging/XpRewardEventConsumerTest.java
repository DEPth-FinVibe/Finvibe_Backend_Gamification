package depth.finvibe.gamification.modules.gamification.infra.messaging;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.in.XpCommandUseCase;
import depth.finvibe.gamification.modules.gamification.domain.error.GamificationErrorCode;
import depth.finvibe.gamification.shared.dto.XpRewardEvent;
import depth.finvibe.gamification.shared.error.DomainException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("XpRewardEventConsumer 단위 테스트")
@ExtendWith(MockitoExtension.class)
class XpRewardEventConsumerTest {

    @Mock
    private XpCommandUseCase xpCommandUseCase;

    @InjectMocks
    private XpRewardEventConsumer xpRewardEventConsumer;

    @Test
    @DisplayName("유효한 XP 이벤트를 수신하면 사용자 XP를 지급한다")
    void consume_xp_reward_event_grants_user_xp() {
        UUID userId = UUID.randomUUID();
        XpRewardEvent event = XpRewardEvent.of(userId.toString(), "챌린지 보상", 100L);

        xpRewardEventConsumer.consumeXpRewardEvent(event);

        verify(xpCommandUseCase).grantUserXp(userId, 100L, "챌린지 보상");
    }

    @Test
    @DisplayName("필수 값이 없으면 XP 지급을 수행하지 않는다")
    void consume_xp_reward_event_returns_when_missing_required_fields() {
        XpRewardEvent event = XpRewardEvent.of(null, "사유", 10L);

        xpRewardEventConsumer.consumeXpRewardEvent(event);

        verify(xpCommandUseCase, never()).grantUserXp(any(), any(), any());
    }

    @Test
    @DisplayName("사용자 UUID 형식이 올바르지 않으면 XP 지급을 수행하지 않는다")
    void consume_xp_reward_event_returns_when_invalid_user_id() {
        XpRewardEvent event = XpRewardEvent.of("invalid-uuid", "사유", 10L);

        xpRewardEventConsumer.consumeXpRewardEvent(event);

        verify(xpCommandUseCase, never()).grantUserXp(any(), any(), any());
    }

    @Test
    @DisplayName("도메인 유효성 예외면 메시지를 무시한다")
    void consume_xp_reward_event_ignores_invalid_domain_exception() {
        UUID userId = UUID.randomUUID();
        XpRewardEvent event = XpRewardEvent.of(userId.toString(), "사유", 0L);
        doThrow(new DomainException(GamificationErrorCode.INVALID_XP_VALUE))
                .when(xpCommandUseCase)
                .grantUserXp(any(), any(), any());

        xpRewardEventConsumer.consumeXpRewardEvent(event);
    }

    @Test
    @DisplayName("허용하지 않은 도메인 예외는 다시 던진다")
    void consume_xp_reward_event_rethrows_unhandled_domain_exception() {
        UUID userId = UUID.randomUUID();
        XpRewardEvent event = XpRewardEvent.of(userId.toString(), "사유", 10L);
        doThrow(new DomainException(GamificationErrorCode.FORBIDDEN_ACCESS))
                .when(xpCommandUseCase)
                .grantUserXp(any(), any(), any());

        assertThatThrownBy(() -> xpRewardEventConsumer.consumeXpRewardEvent(event))
                .isInstanceOf(DomainException.class);
    }
}
