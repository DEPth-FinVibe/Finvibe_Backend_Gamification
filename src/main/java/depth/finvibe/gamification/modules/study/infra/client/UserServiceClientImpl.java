package depth.finvibe.gamification.modules.study.infra.client;

import java.util.List;

import org.springframework.stereotype.Component;

import depth.finvibe.gamification.modules.study.application.port.out.UserServiceClient;

@Component
public class UserServiceClientImpl implements UserServiceClient {

    @Override
    public List<String> fetchUserInterestStocks(String userId) {
        return List.of();
    }
}
