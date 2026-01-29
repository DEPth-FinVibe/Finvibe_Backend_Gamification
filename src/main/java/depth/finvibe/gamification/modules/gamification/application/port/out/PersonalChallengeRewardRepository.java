package depth.finvibe.gamification.modules.gamification.application.port.out;

import java.util.List;

import depth.finvibe.gamification.modules.gamification.domain.PersonalChallengeReward;

public interface PersonalChallengeRewardRepository {
    void saveAll(List<PersonalChallengeReward> rewards);
}
