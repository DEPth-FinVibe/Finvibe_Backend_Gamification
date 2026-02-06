package depth.finvibe.gamification.modules.gamification.infra.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserServiceClient;

@Component
public class GamificationUserServiceClientImpl implements UserServiceClient {

    private final RestClient restClient;

    public GamificationUserServiceClientImpl(

    ) {
        this.restClient = RestClient.builder().baseUrl("http://user").build();
    }

    @Override
    public Optional<String> getNickname(UUID userId) {
        try {
            String nickname = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/members/{userId}/nickname").build(userId))
                .retrieve()
                .body(String.class);
            return Optional.ofNullable(nickname);
        } catch (RestClientException exception) {
            return Optional.empty();
        }
    }
}
