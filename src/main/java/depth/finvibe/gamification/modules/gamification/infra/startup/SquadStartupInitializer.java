package depth.finvibe.gamification.modules.gamification.infra.startup;

import java.nio.charset.StandardCharsets;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import depth.finvibe.gamification.modules.gamification.application.port.out.SquadRepository;
import depth.finvibe.gamification.modules.gamification.domain.Squad;
import depth.finvibe.gamification.shared.lock.DistributedLockManager;
import depth.finvibe.gamification.shared.lock.LockAcquisitionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SquadStartupInitializer implements ApplicationRunner {

    private static final String LOCK_KEY = "gamification_squad_init";
    private static final String SEED_PATH = "classpath:seed/squads.json";

    private final DistributedLockManager distributedLockManager;
    private final SquadRepository squadRepository;
    private final ObjectMapper objectMapper;
    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            distributedLockManager.executeWithLock(LOCK_KEY, () -> {
                // Idempotency 체크
                if (!squadRepository.findAll().isEmpty()) {
                    log.info("Squads already exist. skip initialization");
                    return null;
                }

                log.info("Starting squad initialization");

                // JSON 로드
                SeedSquads seedSquads = loadSeedSquads();

                // 데이터 검증
                if (seedSquads == null || seedSquads.squads == null || seedSquads.squads.isEmpty()) {
                    log.warn("Squad seed data is empty. skip initialization");
                    return null;
                }

                // Squad 생성 및 저장
                for (SeedSquad seedSquad : seedSquads.squads) {
                    if (seedSquad.name != null && !seedSquad.name.isBlank()
                        && seedSquad.region != null && !seedSquad.region.isBlank()) {
                        saveSquad(seedSquad);
                    }
                }

                log.info("Squad initialization completed. total={}", seedSquads.squads.size());
                return null;
            });
        } catch (LockAcquisitionException ex) {
            log.warn("Skip squad initialization due to lock acquisition failure", ex);
        }
    }

    private SeedSquads loadSeedSquads() {
        Resource resource = resourceLoader.getResource(SEED_PATH);
        try {
            byte[] bytes = resource.getInputStream().readAllBytes();
            String json = new String(bytes, StandardCharsets.UTF_8).trim();
            return objectMapper.readValue(json, SeedSquads.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load squad seed data", ex);
        }
    }

    private void saveSquad(SeedSquad seedSquad) {
        Squad squad = Squad.builder()
                .name(seedSquad.name)
                .region(seedSquad.region)
                .build();

        squadRepository.save(squad);
    }

    private static class SeedSquads {
        public List<SeedSquad> squads;
    }

    private static class SeedSquad {
        public String name;
        public String region;
    }
}
