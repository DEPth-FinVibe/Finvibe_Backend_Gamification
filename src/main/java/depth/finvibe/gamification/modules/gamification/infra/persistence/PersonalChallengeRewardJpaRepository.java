package depth.finvibe.gamification.modules.gamification.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.gamification.domain.PersonalChallengeReward;

public interface PersonalChallengeRewardJpaRepository extends JpaRepository<PersonalChallengeReward, Long> {
}
