package depth.finvibe.gamification.modules.gamification.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRankingHistoryRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.SquadXpRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserServiceClient;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserSquadRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpAwardRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserXpRepository;
import depth.finvibe.gamification.modules.gamification.domain.Squad;
import depth.finvibe.gamification.modules.gamification.domain.SquadRankingHistory;
import depth.finvibe.gamification.modules.gamification.domain.SquadXp;
import depth.finvibe.gamification.modules.gamification.domain.UserSquad;
import depth.finvibe.gamification.modules.gamification.domain.UserXp;
import depth.finvibe.gamification.modules.gamification.domain.UserXpAward;
import depth.finvibe.gamification.modules.gamification.dto.XpDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("XpService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class XpServiceTest {

    @Mock
    private UserXpAwardRepository userXpAwardRepository;

    @Mock
    private UserXpRepository userXpRepository;

    @Mock
    private SquadXpRepository squadXpRepository;

    @Mock
    private SquadRankingHistoryRepository squadRankingHistoryRepository;

    @Mock
    private UserSquadRepository userSquadRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private XpService xpService;

    @Test
    @DisplayName("유저 XP 지급 시 유저/스쿼드 XP가 함께 갱신된다")
    void grant_user_xp_updates_user_and_squad() {
        UUID userId = UUID.randomUUID();
        Squad squad = Squad.builder()
                .id(1L)
                .name("스쿼드")
                .region("서울")
                .build();
        UserSquad userSquad = UserSquad.builder()
                .userId(userId)
                .squad(squad)
                .build();

        when(userXpRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userServiceClient.getNickname(userId)).thenReturn(Optional.of("닉네임"));
        when(userSquadRepository.findByUserId(userId)).thenReturn(Optional.of(userSquad));
        when(squadXpRepository.findBySquadId(1L)).thenReturn(Optional.empty());

        xpService.grantUserXp(userId, 1000L, "보상");

        ArgumentCaptor<UserXpAward> awardCaptor = ArgumentCaptor.forClass(UserXpAward.class);
        verify(userXpAwardRepository).save(awardCaptor.capture());
        assertThat(awardCaptor.getValue().getUserId()).isEqualTo(userId);

        ArgumentCaptor<UserXp> userXpCaptor = ArgumentCaptor.forClass(UserXp.class);
        verify(userXpRepository).save(userXpCaptor.capture());
        assertThat(userXpCaptor.getValue().getTotalXp()).isEqualTo(1000L);
        assertThat(userXpCaptor.getValue().getWeeklyXp()).isEqualTo(1000L);
        assertThat(userXpCaptor.getValue().getLevel()).isEqualTo(2);

        ArgumentCaptor<SquadXp> squadXpCaptor = ArgumentCaptor.forClass(SquadXp.class);
        verify(squadXpRepository).save(squadXpCaptor.capture());
        assertThat(squadXpCaptor.getValue().getSquadId()).isEqualTo(1L);
        assertThat(squadXpCaptor.getValue().getTotalXp()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("스쿼드 랭킹 조회 시 이전 랭킹 변화를 반영한다")
    void get_squad_xp_ranking_includes_previous_ranking() {
        Squad squadA = Squad.builder().id(1L).name("A").region("서울").build();
        Squad squadB = Squad.builder().id(2L).name("B").region("경기").build();
        SquadXp xpA = SquadXp.builder().squadId(1L).squad(squadA).totalXp(100L).weeklyXp(10L).build();
        SquadXp xpB = SquadXp.builder().squadId(2L).squad(squadB).totalXp(50L).weeklyXp(5L).build();

        when(squadXpRepository.findAllByOrderByTotalXpDesc()).thenReturn(List.of(xpA, xpB));
        when(squadRankingHistoryRepository.findFirstBySquadIdOrderByRecordDateDescIdDesc(1L))
                .thenReturn(Optional.of(SquadRankingHistory.of(1L, 2, 100L)));
        when(squadRankingHistoryRepository.findFirstBySquadIdOrderByRecordDateDescIdDesc(2L))
                .thenReturn(Optional.empty());

        List<XpDto.SquadRankingResponse> result = xpService.getSquadXpRanking();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRankingChange()).isEqualTo(1);
        assertThat(result.get(1).getRankingChange()).isEqualTo(0);
    }

    @Test
    @DisplayName("스쿼드 기여도 랭킹 조회 시 주간 XP 순으로 반환한다")
    void get_squad_contribution_ranking_orders_by_weekly_xp() {
        UUID userId = UUID.randomUUID();
        UUID member1 = UUID.randomUUID();
        UUID member2 = UUID.randomUUID();
        Squad squad = Squad.builder().id(10L).name("테스트").region("서울").build();
        UserSquad userSquad = UserSquad.builder().userId(userId).squad(squad).build();

        when(userSquadRepository.findByUserId(userId)).thenReturn(Optional.of(userSquad));
        when(userSquadRepository.findAllBySquadId(10L)).thenReturn(List.of(
                UserSquad.builder().userId(member1).squad(squad).build(),
                UserSquad.builder().userId(member2).squad(squad).build()
        ));
        when(userXpRepository.findAllByUserIdInOrderByWeeklyXpDesc(List.of(member1, member2)))
                .thenReturn(List.of(
                        UserXp.builder().userId(member2).weeklyXp(200L).build(),
                        UserXp.builder().userId(member1).weeklyXp(100L).build()
                ));

        List<XpDto.ContributionRankingResponse> result = xpService.getSquadContributionRanking(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRanking()).isEqualTo(1);
        assertThat(result.get(0).getWeeklyContributionXp()).isEqualTo(200L);
        assertThat(result.get(1).getRanking()).isEqualTo(2);
    }

    @Test
    @DisplayName("주간 랭킹 갱신 시 스쿼드/유저 주간 XP를 초기화한다")
    void update_weekly_squad_ranking_resets_weekly_xp() {
        Squad squad = Squad.builder().id(1L).name("A").region("서울").build();
        SquadXp squadXp = SquadXp.builder().squadId(1L).squad(squad).totalXp(100L).weeklyXp(50L).build();
        UserXp userXp = UserXp.builder().userId(UUID.randomUUID()).weeklyXp(30L).build();

        when(squadXpRepository.findAllByOrderByTotalXpDesc()).thenReturn(List.of(squadXp));
        when(userXpRepository.findAll()).thenReturn(List.of(userXp));

        xpService.updateWeeklySquadRanking();

        verify(squadRankingHistoryRepository).save(any(SquadRankingHistory.class));
        verify(squadXpRepository).saveAll(anyList());
        verify(userXpRepository).saveAll(anyList());
        assertThat(squadXp.getWeeklyXp()).isEqualTo(0L);
        assertThat(userXp.getWeeklyXp()).isEqualTo(0L);
    }

    @Test
    @DisplayName("유저 XP 조회 시 존재하지 않으면 새로 생성한다")
    void get_user_xp_creates_when_missing() {
        UUID userId = UUID.randomUUID();
        when(userXpRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userServiceClient.getNickname(userId)).thenReturn(Optional.of("닉네임"));

        XpDto.Response response = xpService.getUserXp(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getTotalXp()).isEqualTo(0L);
        assertThat(response.getLevel()).isEqualTo(1);
    }

    @Test
    @DisplayName("유저 XP 조회 시 기존 데이터를 반환한다")
    void get_user_xp_returns_existing() {
        UUID userId = UUID.randomUUID();
        UserXp userXp = UserXp.builder().userId(userId).totalXp(500L).level(2).build();
        when(userXpRepository.findByUserId(userId)).thenReturn(Optional.of(userXp));

        XpDto.Response response = xpService.getUserXp(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getTotalXp()).isEqualTo(500L);
        assertThat(response.getLevel()).isEqualTo(2);
    }
}
