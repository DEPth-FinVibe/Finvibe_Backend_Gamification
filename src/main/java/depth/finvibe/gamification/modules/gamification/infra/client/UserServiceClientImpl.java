package depth.finvibe.gamification.modules.gamification.infra.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserServiceClient;

@Component
public class UserServiceClientImpl implements UserServiceClient {

    @Override
    public Optional<String> getNickname(UUID userId) {
        return Optional.empty();
    }
}
