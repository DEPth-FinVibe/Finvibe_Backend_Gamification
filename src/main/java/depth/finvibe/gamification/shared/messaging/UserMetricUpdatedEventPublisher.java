package depth.finvibe.gamification.shared.messaging;

import depth.finvibe.gamification.shared.dto.UserMetricUpdatedEvent;

public interface UserMetricUpdatedEventPublisher {
    void publishUserMetricUpdatedEvent(UserMetricUpdatedEvent event);
}
