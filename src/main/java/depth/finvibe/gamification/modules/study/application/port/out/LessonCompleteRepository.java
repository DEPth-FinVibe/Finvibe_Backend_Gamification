package depth.finvibe.gamification.modules.study.application.port.out;

import java.util.List;
import java.util.UUID;

import depth.finvibe.gamification.modules.study.domain.LessonComplete;

public interface LessonCompleteRepository {
    LessonComplete save(LessonComplete lessonComplete);
    boolean existsByLessonUserIdKey(String lessonUserIdKey);
    long countByLessonCourseIdAndUserId(Long courseId, UUID userId);
    List<Long> findLessonIdsByUserIdAndCourseId(UUID userId, Long courseId);
}
