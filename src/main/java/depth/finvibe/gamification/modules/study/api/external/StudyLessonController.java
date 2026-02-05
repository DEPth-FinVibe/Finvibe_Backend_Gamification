package depth.finvibe.gamification.modules.study.api.external;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import depth.finvibe.gamification.boot.security.model.AuthenticatedUser;
import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.application.port.in.CourseQueryUseCase;
import depth.finvibe.gamification.modules.study.application.port.in.LessonQueryUseCase;
import depth.finvibe.gamification.modules.study.dto.LessonDto;

@Tag(name = "학습 레슨", description = "레슨 조회 및 완료 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/study/lessons")
public class StudyLessonController {

    private final LessonQueryUseCase lessonQueryUseCase;
    private final CourseQueryUseCase courseQueryUseCase;

    @Operation(summary = "레슨 상세 조회", description = "레슨 상세와 완료 여부를 조회합니다")
    @GetMapping("/{lessonId}")
    public LessonDto.LessonDetailResponse getLessonDetail(
            @PathVariable Long lessonId,
            @AuthenticatedUser Requester requester
    ) {
        return lessonQueryUseCase.getLessonDetail(lessonId, requester);
    }

    @Operation(summary = "레슨 완료 처리", description = "레슨 완료를 기록하고 경험치 이벤트를 발행합니다")
    @PostMapping("/{lessonId}/complete")
    public void completeLesson(
            @PathVariable Long lessonId,
            @AuthenticatedUser Requester requester
    ) {
        courseQueryUseCase.completeLesson(lessonId, requester);
    }
}
