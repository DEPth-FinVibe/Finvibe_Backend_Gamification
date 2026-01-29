package depth.finvibe.gamification.modules.gamification.application;

import depth.finvibe.gamification.modules.gamification.application.port.in.XpCommandUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.in.XpQueryUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRankingHistoryRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.SquadXpRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserServiceClient;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserSquadRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpAwardRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRepository;
import depth.finvibe.gamification.modules.gamification.domain.SquadRankingHistory;
import depth.finvibe.gamification.modules.gamification.domain.SquadXp;
import depth.finvibe.gamification.modules.gamification.domain.UserSquad;
import depth.finvibe.gamification.modules.gamification.domain.UserXp;
import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;
import depth.finvibe.gamification.modules.gamification.domain.vo.Xp;
import depth.finvibe.gamification.modules.gamification.dto.XpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class XpService implements XpCommandUseCase, XpQueryUseCase {

    private final UserXpAwardRepository userXpAwardRepository;
    private final UserXpRepository userXpRepository;
    private final SquadXpRepository squadXpRepository;
    private final SquadRankingHistoryRepository squadRankingHistoryRepository;
    private final UserSquadRepository userSquadRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public void grantUserXp(UUID userId, Long value, String reason) {
        Xp xp = Xp.of(value, reason);
        UserXpAward userXpAward = UserXpAward.of(userId, xp);
        userXpAwardRepository.save(userXpAward);

        updateUserXp(userId, value);
        updateSquadXp(userId, value);
    }

    @Override
    @Transactional
    public void updateWeeklySquadRanking() {
        settleSquadRankingsAndResetWeeklyXp();
        resetAllUsersWeeklyXp();
    }

    @Override
    @Transactional(readOnly = true)
    public List<XpDto.SquadRankingResponse> getSquadXpRanking() {
        List<SquadXp> allSquadXp = squadXpRepository.findAllByOrderByTotalXpDesc();

        List<XpDto.SquadRankingResponse> result = new ArrayList<>(allSquadXp.size());
        for (int i = 0; i < allSquadXp.size(); i++) {
            SquadXp squadXp = allSquadXp.get(i);
            int currentRanking = i + 1;
            int previousRanking = squadRankingHistoryRepository
                    .findFirstBySquadIdOrderByRecordDateDescIdDesc(squadXp.getSquadId())
                    .map(SquadRankingHistory::getRanking)
                    .orElse(currentRanking);

            result.add(toSquadRankingResponse(squadXp, currentRanking, previousRanking));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<XpDto.ContributionRankingResponse> getSquadContributionRanking(UUID userId) {
        UserSquad userSquad = userSquadRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 속한 스쿼드가 없습니다."));

        Long squadId = userSquad.getSquad().getId();
        List<UserSquad> squadMembers = userSquadRepository.findAllBySquadId(squadId);

        List<UUID> memberIds = new ArrayList<>(squadMembers.size());
        for (UserSquad member : squadMembers) {
            memberIds.add(member.getUserId());
        }

        List<UserXp> memberXps = userXpRepository.findAllByUserIdInOrderByWeeklyXpDesc(memberIds);

        List<XpDto.ContributionRankingResponse> result = new ArrayList<>(memberXps.size());
        for (int i = 0; i < memberXps.size(); i++) {
            UserXp userXp = memberXps.get(i);
            result.add(toContributionRankingResponse(userXp, i + 1));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public XpDto.Response getUserXp(UUID userId) {
        UserXp userXp = findOrCreateUserXp(userId);

        return XpDto.Response.builder()
                .userId(userXp.getUserId())
                .totalXp(userXp.getTotalXp())
                .level(userXp.getLevel())
                .build();
    }

    private void updateUserXp(UUID userId, Long amount) {
        UserXp userXp = findOrCreateUserXp(userId);
        userXp.addXp(amount);
        userXpRepository.save(userXp);
    }

    private void updateSquadXp(UUID userId, Long amount) {
        userSquadRepository.findByUserId(userId).ifPresent(userSquad -> {
            SquadXp squadXp = squadXpRepository.findBySquadId(userSquad.getSquad().getId())
                    .orElseGet(() -> SquadXp.builder()
                            .squadId(userSquad.getSquad().getId())
                            .squad(userSquad.getSquad())
                            .build());
            squadXp.addXp(amount);
            squadXpRepository.save(squadXp);
        });
    }

    private UserXp findOrCreateUserXp(UUID userId) {
        if (userXpRepository.findByUserId(userId).isPresent()) {
            return userXpRepository.findByUserId(userId).get();
        }

        String nickname = userServiceClient.getNickname(userId)
                .orElseGet(() -> {
                    log.error("사용자 닉네임 조회 실패: {}", userId);
                    return "이름 없음";
                });
                
        return UserXp.of(userId, nickname);
    }

    private void settleSquadRankingsAndResetWeeklyXp() {
        List<SquadXp> allSquadXp = squadXpRepository.findAllByOrderByTotalXpDesc();

        for (int i = 0; i < allSquadXp.size(); i++) {
            SquadXp squadXp = allSquadXp.get(i);
            int ranking = i + 1;

            saveRankingHistory(squadXp, ranking);
            squadXp.resetWeeklyXp();
        }
        squadXpRepository.saveAll(allSquadXp);
    }

    private void resetAllUsersWeeklyXp() {
        List<UserXp> allUserXp = userXpRepository.findAll();
        allUserXp.forEach(UserXp::resetWeeklyXp);
        userXpRepository.saveAll(allUserXp);
    }

    private void saveRankingHistory(SquadXp squadXp, int ranking) {
        SquadRankingHistory history = SquadRankingHistory.of(
                squadXp.getSquadId(),
                ranking,
                squadXp.getTotalXp());
        squadRankingHistoryRepository.save(history);
    }

    private XpDto.SquadRankingResponse toSquadRankingResponse(
            SquadXp squadXp,
            int currentRanking,
            int previousRanking) {
        return XpDto.SquadRankingResponse.builder()
                .squadId(squadXp.getSquadId())
                .squadName(squadXp.getSquad().getName())
                .currentRanking(currentRanking)
                .totalXp(squadXp.getTotalXp())
                .weeklyXp(squadXp.getWeeklyXp())
                .weeklyXpChangeRate(squadXp.getWeeklyXpChangeRate())
                .rankingChange(previousRanking - currentRanking)
                .build();
    }

    private XpDto.ContributionRankingResponse toContributionRankingResponse(UserXp userXp, int ranking) {
        // TODO: 유저 닉네임 모듈 연동 필요 (현재는 userId를 사용)
        return XpDto.ContributionRankingResponse.builder()
                .nickname(userXp.getUserId().toString())
                .ranking(ranking)
                .weeklyContributionXp(userXp.getWeeklyXp())
                .build();
    }
}
