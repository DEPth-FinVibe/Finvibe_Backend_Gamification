package depth.finvibe.gamification.modules.gamification.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import depth.finvibe.gamification.modules.gamification.application.port.in.XpCommandUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.in.XpQueryUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpAwardRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRepository;
import depth.finvibe.gamification.modules.gamification.domain.UserXp;
import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;
import depth.finvibe.gamification.modules.gamification.domain.vo.Xp;
import depth.finvibe.gamification.modules.gamification.dto.XpDto;
import depth.finvibe.gamification.shared.dto.XpRewardEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class XpService implements XpCommandUseCase, XpQueryUseCase {

    private final UserXpAwardRepository userXpAwardRepository;
    private final UserXpRepository userXpRepository;

    @Override
    @Transactional
    public void grantUserXp(UUID userId, Long value, String reason) {
        Xp xp = Xp.of(value, reason);
        UserXpAward userXpAward = UserXpAward.of(userId, xp);

        userXpAwardRepository.save(userXpAward);

        // UserXp 업데이트 (총 XP 및 레벨)
        updateUserXp(userId, value);
    }

    private void updateUserXp(UUID userId, Long amount) {
        UserXp userXp = userXpRepository.findByUserId(userId)
                .orElseGet(() -> UserXp.builder().userId(userId).build());
        userXp.addXp(amount);
        userXpRepository.save(userXp);
    }

    @Override
    @Transactional(readOnly = true)
    public XpDto.Response getUserXp(UUID userId) {
        UserXp userXp = userXpRepository.findByUserId(userId)
                .orElseGet(() -> UserXp.builder().userId(userId).build());

        return XpDto.Response.builder()
                .userId(userXp.getUserId())
                .totalXp(userXp.getTotalXp())
                .level(userXp.getLevel())
                .build();
    }
}
