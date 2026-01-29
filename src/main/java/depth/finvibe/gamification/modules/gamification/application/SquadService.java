package depth.finvibe.gamification.modules.gamification.application;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.boot.security.model.UserRole;
import depth.finvibe.gamification.modules.gamification.application.port.in.SquadCommandUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.in.SquadQueryUseCase;
import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRepository;
import depth.finvibe.gamification.modules.gamification.application.port.out.UserSquadRepository;
import depth.finvibe.gamification.modules.gamification.domain.Squad;
import depth.finvibe.gamification.modules.gamification.domain.UserSquad;
import depth.finvibe.gamification.modules.gamification.dto.SquadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SquadService implements SquadCommandUseCase, SquadQueryUseCase {

    private final SquadRepository squadRepository;
    private final UserSquadRepository userSquadRepository;

    @Override
    @Transactional
    public void joinSquad(Long squadId, Requester requester) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스쿼드입니다."));

        UserSquad userSquad = userSquadRepository.findByUserId(requester.getUuid())
                .orElseGet(() -> UserSquad.builder().userId(requester.getUuid()).build());

        userSquad.changeSquad(squad);
        userSquadRepository.save(userSquad);
    }

    @Override
    @Transactional
    public Long createSquad(String name, String region, Requester requester) {
        validateAdmin(requester);

        Squad squad = Squad.builder()
                .name(name)
                .region(region)
                .build();

        squadRepository.save(squad);
        return squad.getId();
    }

    @Override
    @Transactional
    public void updateSquad(Long squadId, String name, String region, Requester requester) {
        validateAdmin(requester);

        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스쿼드입니다."));

        squad.updateInfo(name, region);
        squadRepository.save(squad);
    }

    @Override
    @Transactional
    public void deleteSquad(Long squadId, Requester requester) {
        validateAdmin(requester);

        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스쿼드입니다."));

        squadRepository.delete(squad);
    }

    private void validateAdmin(Requester requester) {
        if (!requester.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("관리자만 접근 가능합니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SquadDto.Response getUserSquad(UUID userId) {
        UserSquad userSquad = userSquadRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("소속된 스쿼드가 없습니다."));

        Squad squad = userSquad.getSquad();
        return SquadDto.Response.builder()
                .id(squad.getId())
                .name(squad.getName())
                .region(squad.getRegion())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SquadDto.Response> getAllSquads() {
        return squadRepository.findAll().stream()
                .map(squad -> SquadDto.Response.builder()
                        .id(squad.getId())
                        .name(squad.getName())
                        .region(squad.getRegion())
                        .build())
                .collect(Collectors.toList());
    }
}
