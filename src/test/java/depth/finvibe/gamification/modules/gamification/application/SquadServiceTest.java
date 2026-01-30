package depth.finvibe.gamification.modules.gamification.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.boot.security.model.UserRole;
import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserSquadRepository;
import depth.finvibe.gamification.modules.gamification.domain.Squad;
import depth.finvibe.gamification.modules.gamification.domain.UserSquad;
import depth.finvibe.gamification.modules.gamification.dto.SquadDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SquadService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class SquadServiceTest {

    @Mock
    private SquadRepository squadRepository;

    @Mock
    private UserSquadRepository userSquadRepository;

    @InjectMocks
    private SquadService squadService;

    @Test
    @DisplayName("스쿼드 가입 시 유저 스쿼드가 저장된다")
    void join_squad_saves_user_squad() {
        UUID userId = UUID.randomUUID();
        Squad squad = Squad.builder().id(1L).name("스쿼드").region("서울").build();
        when(squadRepository.findById(1L)).thenReturn(Optional.of(squad));
        when(userSquadRepository.findByUserId(userId)).thenReturn(Optional.empty());

        squadService.joinSquad(1L, requester(userId, UserRole.USER));

        verify(userSquadRepository).save(any(UserSquad.class));
    }

    @Test
    @DisplayName("스쿼드 가입 시 스쿼드가 없으면 예외가 발생한다")
    void join_squad_throws_when_squad_missing() {
        UUID userId = UUID.randomUUID();
        when(squadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> squadService.joinSquad(1L, requester(userId, UserRole.USER)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("관리자가 스쿼드를 생성하면 ID를 반환한다")
    void create_squad_returns_id() {
        doAnswer(invocation -> {
            Squad squad = invocation.getArgument(0);
            ReflectionTestUtils.setField(squad, "id", 99L);
            return null;
        }).when(squadRepository).save(any(Squad.class));

        Long result = squadService.createSquad("스쿼드", "서울", requester(UUID.randomUUID(), UserRole.ADMIN));

        assertThat(result).isEqualTo(99L);
    }

    @Test
    @DisplayName("관리자가 아니면 스쿼드 생성이 불가하다")
    void create_squad_throws_when_not_admin() {
        assertThatThrownBy(() -> squadService.createSquad("스쿼드", "서울", requester(UUID.randomUUID(), UserRole.USER)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(squadRepository, never()).save(any());
    }

    @Test
    @DisplayName("관리자가 스쿼드를 수정하면 정보가 변경된다")
    void update_squad_updates_info() {
        Squad squad = Squad.builder().id(1L).name("이전").region("부산").build();
        when(squadRepository.findById(1L)).thenReturn(Optional.of(squad));

        squadService.updateSquad(1L, "변경", "서울", requester(UUID.randomUUID(), UserRole.ADMIN));

        assertThat(squad.getName()).isEqualTo("변경");
        assertThat(squad.getRegion()).isEqualTo("서울");
        verify(squadRepository).save(squad);
    }

    @Test
    @DisplayName("관리자가 아니면 스쿼드 수정이 불가하다")
    void update_squad_throws_when_not_admin() {
        assertThatThrownBy(() -> squadService.updateSquad(1L, "변경", "서울", requester(UUID.randomUUID(), UserRole.USER)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("관리자가 스쿼드를 삭제하면 삭제된다")
    void delete_squad_deletes() {
        Squad squad = Squad.builder().id(1L).name("스쿼드").region("서울").build();
        when(squadRepository.findById(1L)).thenReturn(Optional.of(squad));

        squadService.deleteSquad(1L, requester(UUID.randomUUID(), UserRole.ADMIN));

        verify(squadRepository).delete(squad);
    }

    @Test
    @DisplayName("관리자가 아니면 스쿼드 삭제가 불가하다")
    void delete_squad_throws_when_not_admin() {
        assertThatThrownBy(() -> squadService.deleteSquad(1L, requester(UUID.randomUUID(), UserRole.USER)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("유저 스쿼드 조회 시 스쿼드 정보를 반환한다")
    void get_user_squad_returns_response() {
        UUID userId = UUID.randomUUID();
        Squad squad = Squad.builder().id(1L).name("스쿼드").region("서울").build();
        UserSquad userSquad = UserSquad.builder().userId(userId).squad(squad).build();
        when(userSquadRepository.findByUserId(userId)).thenReturn(Optional.of(userSquad));

        SquadDto.Response response = squadService.getUserSquad(userId);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("스쿼드");
    }

    @Test
    @DisplayName("전체 스쿼드 조회 시 목록을 반환한다")
    void get_all_squads_returns_list() {
        when(squadRepository.findAll()).thenReturn(List.of(
                Squad.builder().id(1L).name("A").region("서울").build(),
                Squad.builder().id(2L).name("B").region("경기").build()
        ));

        List<SquadDto.Response> result = squadService.getAllSquads();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    private Requester requester(UUID userId, UserRole role) {
        return Requester.builder()
                .uuid(userId)
                .role(role)
                .build();
    }
}
