package depth.finvibe.gamification.modules.study.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import depth.finvibe.gamification.modules.study.application.port.out.CourseRepository;
import depth.finvibe.gamification.modules.study.domain.Course;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {
    private final CourseJpaRepository courseJpaRepository;

    @Override
    public Course save(Course course) {
        return courseJpaRepository.save(course);
    }
}
