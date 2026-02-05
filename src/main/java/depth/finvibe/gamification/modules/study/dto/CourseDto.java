package depth.finvibe.gamification.modules.study.dto;

import java.util.List;

import depth.finvibe.gamification.modules.study.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import depth.finvibe.gamification.modules.study.domain.CourseDifficulty;
import depth.finvibe.gamification.modules.study.dto.LessonDto;

public class CourseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateRequest {
        private String title;
        private List<String> keywords;
        private CourseDifficulty difficulty;
    }

    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @Data
    @Builder
    public static class ContentPreviewResponse {
        private String content;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class MyCourseResponse {
        private Long id;
        private String title;
        private String description;
        private CourseDifficulty difficulty;
        private Integer totalLessonCount;
        private List<LessonDto.LessonSummary> lessons;

        public static MyCourseResponse from(Course course, List<LessonDto.LessonSummary> lessons) {
            return MyCourseResponse.builder()
                    .id(course.getId())
                    .title(course.getTitle())
                    .description(course.getDescription())
                    .difficulty(course.getDifficulty())
                    .totalLessonCount(course.getTotalLessonCount())
                    .lessons(lessons)
                    .build();
        }
    }
}
