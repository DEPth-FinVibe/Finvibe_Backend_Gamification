package depth.finvibe.gamification.modules.study.infra.persistence;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.study.application.port.out.LessonCompleteRepository;
import depth.finvibe.gamification.modules.study.domain.LessonComplete;

@Repository
@RequiredArgsConstructor
public class LessonCompleteRepositoryImpl implements LessonCompleteRepository {
    private final LessonCompleteJpaRepository lessonCompleteJpaRepository;

    @Override
    public LessonComplete save(LessonComplete lessonComplete) {
        return lessonCompleteJpaRepository.save(lessonComplete);
    }

    @Override
    public boolean existsByLessonUserIdKey(String lessonUserIdKey) {
        return lessonCompleteJpaRepository.existsByLessonUserIdKey(lessonUserIdKey);
    }

    @Override
    public long countByLessonCourseIdAndUserId(Long courseId, UUID userId) {
        return lessonCompleteJpaRepository.countByLessonCourseIdAndUserId(courseId, userId);
    }
}
