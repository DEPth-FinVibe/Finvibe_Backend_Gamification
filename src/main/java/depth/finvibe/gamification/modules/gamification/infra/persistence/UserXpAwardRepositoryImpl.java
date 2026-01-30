package depth.finvibe.gamification.modules.gamification.infra.persistence;

import java.util.List;
import java.util.UUID;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpAwardRepository;
import depth.finvibe.gamification.modules.gamification.domain.QUserXpAward;
import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;

@Repository
@RequiredArgsConstructor
public class UserXpAwardRepositoryImpl implements UserXpAwardRepository {
    private final UserXpAwardJpaRepository userXpAwardJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void save(UserXpAward userXpAward) {
        userXpAwardJpaRepository.save(userXpAward);
    }

    @Override
    public List<UserXpAward> findByUserId(UUID userId) {
        return userXpAwardJpaRepository.findByUserId(userId);
    }

    @Override
    public List<UUID> findTopUsersByTotalXp(int limit) {
        QUserXpAward userXpAward = QUserXpAward.userXpAward;

        return jpaQueryFactory.select(userXpAward.userId)
                .from(userXpAward)
                .groupBy(userXpAward.userId)
                .orderBy(userXpAward.xp.value.sum().desc())
                .limit(limit)
                .fetch();
    }
}
