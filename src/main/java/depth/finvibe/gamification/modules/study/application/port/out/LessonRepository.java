package depth.finvibe.gamification.modules.study.application.port.out;

import depth.finvibe.gamification.modules.study.domain.Lesson;

import java.util.List;

public interface LessonRepository {
    Lesson save(Lesson lesson);
    List<Lesson> saveAll(List<Lesson> lessons);
}
