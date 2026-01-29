package depth.finvibe.gamification.modules.gamification.application;

import java.util.UUID;

import depth.finvibe.gamification.modules.gamification.application.port.in.XpCommandUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpAwardRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.XpRewardEventPublisher;
import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;
import depth.finvibe.gamification.modules.gamification.domain.vo.Xp;
import depth.finvibe.gamification.shared.dto.XpRewardEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class XpService implements XpCommandUseCase {

    private final UserXpAwardRepository userXpAwardRepository;
    private final XpRewardEventPublisher xpRewardEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void grantUserXp(UUID userId, Long value, String reason) {
        Xp xp = Xp.of(value, reason);
        UserXpAward userXpAward = UserXpAward.builder()
                .userId(userId)
                .xp(xp)
                .build();

        userXpAwardRepository.save(userXpAward);

        // XP 보상 이벤트 발행
        publishXpRewardEvent(userId, reason, value);
    }

    private void publishXpRewardEvent(UUID userId, String reason, Long rewardXp) {
        // 어플리케이션 이벤트로 일단 처리
        applicationEventPublisher.publishEvent(
                XpRewardEvent.of(
                        userId.toString(),
                        reason,
                        rewardXp
                )
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleXpRewardEvent(XpRewardEvent event) {
        // 커밋이 완료된 후 외부 이벤트를 발행
        xpRewardEventPublisher.publishXpRewardEvent(event);
    }
}