package depth.finvibe.gamification.modules.study.application.port.in;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.dto.LessonDto;

public interface LessonQueryUseCase {
    LessonDto.LessonDetailResponse getLessonDetail(Long lessonId, Requester requester);
}
