package depth.finvibe.gamification.modules.study.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import depth.finvibe.gamification.modules.study.domain.CourseDifficulty;

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
}
