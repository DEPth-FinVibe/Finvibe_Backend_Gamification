package depth.finvibe.gamification.modules.study.dto;

import depth.finvibe.gamification.modules.study.domain.Lesson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class GeneratorDto {
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @Data
    @Builder
    public static class LessonIndex {
        private String title;
        private String description;

        public static LessonIndex from(Lesson lesson) {
            return LessonIndex.builder()
                    .title(lesson.getTitle())
                    .description(lesson.getDescription())
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LessonIndexCreateRequest {
        private String courseTitle;
        private List<String> keywords;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LessonContentCreateContext {
        private String courseTitle;
        private List<String> keywords;
        private String lessonTitle;
        private String lessonDescription;
    }
}
