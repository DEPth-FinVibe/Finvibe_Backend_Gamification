package depth.finvibe.gamification.modules.study.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import depth.finvibe.gamification.modules.study.domain.Course;

public interface CourseJpaRepository extends JpaRepository<Course, Long> {
    boolean existsByIsGlobalTrue();
}
