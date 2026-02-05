package depth.finvibe.gamification.modules.study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CourseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateRequest {
        private String title;
        private List<String> keywords;
    }



    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    @Data
    @Builder
    public static class ContentPreviewResponse {
        private String content;
    }
}
