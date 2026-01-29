package depth.finvibe.gamification.modules.gamification.application.port.out;

import depth.finvibe.gamification.modules.gamification.dto.ChallengeDto;

import java.util.List;

public interface ChallengeGenerator {

    List<ChallengeDto.ChallengeGenerationResponse> generate();

}
