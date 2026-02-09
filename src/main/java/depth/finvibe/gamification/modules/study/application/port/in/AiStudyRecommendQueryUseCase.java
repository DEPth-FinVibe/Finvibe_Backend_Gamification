package depth.finvibe.gamification.modules.study.application.port.in;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.dto.AiStudyRecommendDto;

public interface AiStudyRecommendQueryUseCase {
    AiStudyRecommendDto.GetTodayAiStudyRecommendResponse getTodayAiStudyRecommend(Requester requester);
}
