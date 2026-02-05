package depth.finvibe.gamification.modules.study.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.study.domain.LessonComplete;

public interface LessonCompleteJpaRepository extends JpaRepository<LessonComplete, Long> {
    boolean existsByLessonUserIdKey(String lessonUserIdKey);
    long countByLessonCourseIdAndUserId(Long courseId, UUID userId);
}
