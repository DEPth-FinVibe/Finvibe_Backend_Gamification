package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.PersonalChallengeRewardRepository;
import depth.finvibe.gamification.modules.gamification.domain.PersonalChallengeReward;

@Repository
@RequiredArgsConstructor
public class PersonalChallengeRewardRepositoryImpl implements PersonalChallengeRewardRepository {
    private final PersonalChallengeRewardJpaRepository personalChallengeRewardJpaRepository;

    @Override
    public void saveAll(List<PersonalChallengeReward> rewards) {
        personalChallengeRewardJpaRepository.saveAll(rewards);
    }
}
