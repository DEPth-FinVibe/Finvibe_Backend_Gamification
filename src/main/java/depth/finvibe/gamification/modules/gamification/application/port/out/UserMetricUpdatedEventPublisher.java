package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.shared.dto.UserMetricUpdatedEvent;

public interface UserMetricUpdatedEventPublisher {
    void publishUserMetricUpdatedEvent(UserMetricUpdatedEvent event);
}
