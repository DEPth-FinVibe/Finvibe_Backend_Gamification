package depth.finvibe.gamification.modules.study.application.port.in;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.dto.CourseDto;

import java.util.List;

public interface CourseQueryUseCase {
    List<String> getRecommendedKeywords(Requester requester);
    CourseDto.ContentPreviewResponse previewCourseContent(CourseDto.CreateRequest request, Requester requester);
}
