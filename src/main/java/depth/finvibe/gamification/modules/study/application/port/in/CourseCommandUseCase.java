package depth.finvibe.gamification.modules.study.application.port.in;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.dto.CourseDto;

public interface CourseCommandUseCase {
    void createCourse(CourseDto.CreateRequest request, Requester requester);
    void completeLesson(Long lessonId, Requester requester);
}
