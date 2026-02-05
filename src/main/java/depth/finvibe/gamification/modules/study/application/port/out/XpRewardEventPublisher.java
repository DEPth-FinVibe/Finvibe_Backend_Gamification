package depth.finvibe.gamification.modules.study.application.port.out;

import depth.finvibe.gamification.shared.dto.XpRewardEvent;

public interface XpRewardEventPublisher {
    void publishXpRewardEvent(XpRewardEvent xpRewardEvent);
}
