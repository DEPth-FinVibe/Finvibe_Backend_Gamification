package depth.finvibe.gamification.modules.study.application.port.out;

import depth.finvibe.gamification.modules.study.domain.Course;

public interface CourseRepository {
    Course save(Course course);
}
