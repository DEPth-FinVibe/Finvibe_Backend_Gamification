package depth.finvibe.gamification.modules.study.application;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.application.port.in.CourseCommandUseCase;
import depth.finvibe.gamification.modules.study.application.port.in.CourseQueryUseCase;
import depth.finvibe.gamification.modules.study.application.port.out.*;
import depth.finvibe.gamification.modules.study.domain.Course;
import depth.finvibe.gamification.modules.study.domain.Lesson;
import depth.finvibe.gamification.modules.study.domain.LessonContent;
import depth.finvibe.gamification.modules.study.dto.CourseDto;
import depth.finvibe.gamification.modules.study.dto.GeneratorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CourseService implements CourseCommandUseCase, CourseQueryUseCase {

    private final UserServiceClient userServiceClient;
    private final KeywordGenerator keywordGenerator;
    private final CourseGenerator courseGenerator;
    private final LessonGenerator lessonGenerator;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void createCourse(CourseDto.CreateRequest request, Requester requester) {
        GeneratorDto.LessonIndexCreateRequest lessonRequest = toIndexCreateRequest(request);

        List<GeneratorDto.LessonIndex> lessonIndices = lessonGenerator.generateLessonIndex(lessonRequest);
        String courseDescription = courseGenerator.generateCourseDescription(request.getTitle(), request.getKeywords(), lessonIndices);

        Course savedCourse = saveCourseFromRequest(request, requester, courseDescription);
        List<Lesson> savedLessons = generateLessonsFromIndices(lessonIndices, savedCourse);

        List<LessonContentTask> contentTasks = savedLessons.stream()
                .map(lesson -> createLessonContentTask(request, savedCourse, lesson))
                .toList();

        waitForAllContent(contentTasks);
        applyLessonContents(contentTasks);
    }

    private LessonContentTask createLessonContentTask(
            CourseDto.CreateRequest request,
            Course savedCourse,
            Lesson lesson
    ) {
        GeneratorDto.LessonContentCreateContext context = GeneratorDto.LessonContentCreateContext.builder()
                .courseTitle(savedCourse.getTitle())
                .keywords(request.getKeywords())
                .lessonTitle(lesson.getTitle())
                .lessonDescription(lesson.getDescription())
                .build();

        CompletableFuture<String> future = lessonGenerator.generateLessonContent(context);
        return new LessonContentTask(lesson, future);
    }

    private void waitForAllContent(List<LessonContentTask> tasks) {
        CompletableFuture<?>[] futures = tasks.stream()
                .map(LessonContentTask::future)
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }

    private void applyLessonContents(List<LessonContentTask> tasks) {
        tasks.forEach(task -> {
            String content = task.future().join();
            LessonContent lessonContent = LessonContent.of(content);
            task.lesson().makeRelationshipWith(lessonContent);
        });
    }

    private record LessonContentTask(Lesson lesson, CompletableFuture<String> future) {}

    private List<Lesson> generateLessonsFromIndices(List<GeneratorDto.LessonIndex> lessonIndices, Course savedCourse) {
        List<Lesson> lessons = lessonIndices.stream()
                .map(idx -> Lesson.of(savedCourse, idx.getTitle(), idx.getDescription()))
                .toList();
        return lessonRepository.saveAll(lessons);
    }

    private Course saveCourseFromRequest(CourseDto.CreateRequest request, Requester requester, String courseDescription) {
        Course courseToSave = Course.of(request.getTitle(), courseDescription, requester.getUuid());
        return courseRepository.save(courseToSave);
    }

    private static GeneratorDto.LessonIndexCreateRequest toIndexCreateRequest(CourseDto.CreateRequest request) {
        return GeneratorDto.LessonIndexCreateRequest.builder()
                .courseTitle(request.getTitle())
                .keywords(request.getKeywords())
                .build();
    }

    @Override
    public List<String> getRecommendedKeywords(Requester requester) {
        List<String> fetchedStockNames = userServiceClient.fetchUserInterestStocks(requester.getUuid().toString());

        return keywordGenerator.generateKeywords(fetchedStockNames);
    }

    @Override
    public CourseDto.ContentPreviewResponse previewCourseContent(CourseDto.CreateRequest request, Requester requester) {
        String previewContent = courseGenerator.generateCoursePreview(request.getTitle(), request.getKeywords());

        return CourseDto.ContentPreviewResponse.of(previewContent);
    }
}
