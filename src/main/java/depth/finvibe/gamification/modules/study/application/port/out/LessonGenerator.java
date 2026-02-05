package depth.finvibe.gamification.modules.study.application.port.out;

import depth.finvibe.gamification.modules.study.dto.GeneratorDto;

import java.util.List;

public interface LessonGenerator {
    List<GeneratorDto.LessonIndex> generateLessonIndex(GeneratorDto.LessonIndexCreateRequest request);

    String generateLessonContent(GeneratorDto.LessonContentCreateContext context);
}
