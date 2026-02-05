package depth.finvibe.gamification.modules.study.dto;

import depth.finvibe.gamification.modules.study.domain.Lesson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LessonDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LessonSummary {
        private Long id;
        private String title;
        private String description;
        private boolean completed;

        public static LessonSummary from(Lesson lesson, boolean completed) {
            return LessonSummary.builder()
                    .id(lesson.getId())
                    .title(lesson.getTitle())
                    .description(lesson.getDescription())
                    .completed(completed)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LessonDetailResponse {
        private Long id;
        private String title;
        private String description;
        private String content;
        private boolean completed;

        public static LessonDetailResponse from(Lesson lesson, String content, boolean completed) {
            return LessonDetailResponse.builder()
                    .id(lesson.getId())
                    .title(lesson.getTitle())
                    .description(lesson.getDescription())
                    .content(content)
                    .completed(completed)
                    .build();
        }
    }
}
