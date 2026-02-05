package depth.finvibe.gamification.modules.study.infra.persistence;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.study.application.port.out.CourseProgressRepository;
import depth.finvibe.gamification.modules.study.domain.CourseProgress;

@Repository
@RequiredArgsConstructor
public class CourseProgressRepositoryImpl implements CourseProgressRepository {
    private final CourseProgressJpaRepository courseProgressJpaRepository;

    @Override
    public CourseProgress save(CourseProgress courseProgress) {
        return courseProgressJpaRepository.save(courseProgress);
    }

    @Override
    public Optional<CourseProgress> findByCourseUserIdKey(String courseUserIdKey) {
        return courseProgressJpaRepository.findByCourseUserIdKey(courseUserIdKey);
    }

    @Override
    public Optional<CourseProgress> findByCourseIdAndUserId(Long courseId, UUID userId) {
        return courseProgressJpaRepository.findByCourseIdAndUserId(courseId, userId);
    }
}
