package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.shared.dto.XpRewardEvent;

public interface XpRewardEventPublisher {
    void publishXpRewardEvent(XpRewardEvent xpRewardEvent);
}
