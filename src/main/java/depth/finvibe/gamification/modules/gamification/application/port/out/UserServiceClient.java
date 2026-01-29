package depth.finvibe.gamification.modules.gamification.application.port.out;

import java.util.UUID;
import java.util.Optional;

public interface UserServiceClient {
    Optional<String> getNickname(UUID userId);
}
