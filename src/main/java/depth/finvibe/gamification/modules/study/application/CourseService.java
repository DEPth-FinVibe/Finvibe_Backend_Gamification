package depth.finvibe.gamification.modules.study.application;

import depth.finvibe.gamification.boot.security.model.Requester;
import depth.finvibe.gamification.modules.study.application.port.in.CourseCommandUseCase;
import depth.finvibe.gamification.modules.study.application.port.in.CourseQueryUseCase;
import depth.finvibe.gamification.modules.study.application.port.in.LessonQueryUseCase;
import depth.finvibe.gamification.modules.study.application.port.out.*;
import depth.finvibe.gamification.modules.study.domain.*;
import depth.finvibe.gamification.modules.study.dto.CourseDto;
import depth.finvibe.gamification.modules.study.dto.GeneratorDto;
import depth.finvibe.gamification.modules.study.dto.LessonDto;
import depth.finvibe.gamification.shared.dto.XpRewardEvent;
import depth.finvibe.gamification.shared.error.DomainException;
import depth.finvibe.gamification.shared.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CourseService implements CourseCommandUseCase, CourseQueryUseCase, LessonQueryUseCase {

    private final UserServiceClient userServiceClient;
    private final KeywordGenerator keywordGenerator;
    private final CourseGenerator courseGenerator;
    private final LessonGenerator lessonGenerator;
    private final LessonRepository lessonRepository;
    private final LessonCompleteRepository lessonCompleteRepository;
    private final CourseProgressRepository courseProgressRepository;
    private final CourseRepository courseRepository;
    private final XpRewardEventPublisher xpRewardEventPublisher;

    @Override
    @Transactional
    public void createCourse(CourseDto.CreateRequest request, Requester requester) {
        GeneratorDto.LessonIndexCreateRequest lessonRequest = toIndexCreateRequest(request);

        List<GeneratorDto.LessonIndex> lessonIndices = lessonGenerator.generateLessonIndex(lessonRequest);
        String courseDescription = courseGenerator.generateCourseDescription(request.getTitle(), request.getKeywords(), lessonIndices);

        Course savedCourse = saveCourseFromRequest(request, requester, courseDescription);
        List<Lesson> savedLessons = generateLessonsFromIndices(lessonIndices, savedCourse);
        savedCourse.updateTotalLessonCount(savedLessons.size());

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
        CourseDifficulty difficulty = request.getDifficulty() == null
                ? CourseDifficulty.BEGINNER
                : request.getDifficulty();
        Course courseToSave = Course.of(request.getTitle(), courseDescription, difficulty, requester.getUuid());
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

    @Override
    @Transactional(readOnly = true)
    public List<CourseDto.MyCourseResponse> getMyCourses(Requester requester) {
        List<Course> courses = courseRepository.findByOwnerOrIsGlobalTrue(requester.getUuid());

        return courses.stream()
                .map(course -> toMyCourseResponse(course, requester))
                .toList();
    }

    @Override
    @Transactional
    public void completeLesson(Long lessonId, Requester requester) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DomainException(GlobalErrorCode.NOT_FOUND));

        String lessonUserIdKey = LessonComplete.generateLessonUserIdKey(lessonId, requester.getUuid());
        if (lessonCompleteRepository.existsByLessonUserIdKey(lessonUserIdKey)) {
            return;
        }

        LessonComplete lessonComplete = LessonComplete.of(lesson, requester.getUuid());
        lessonCompleteRepository.save(lessonComplete);

        Course course = lesson.getCourse();
        if (course == null) {
            return;
        }

        Integer totalLessonCount = course.getTotalLessonCount();
        if (totalLessonCount == null || totalLessonCount == 0) {
            int totalCount = (int) lessonRepository.countByCourseId(course.getId());
            course.updateTotalLessonCount(totalCount);
        }

        int completedCount = (int) lessonCompleteRepository.countByLessonCourseIdAndUserId(
                course.getId(),
                requester.getUuid()
        );

        String courseUserIdKey = CourseProgress.generateCourseUserIdKey(course.getId(), requester.getUuid());
        CourseProgress courseProgress = courseProgressRepository.findByCourseUserIdKey(courseUserIdKey)
                .orElseGet(() -> CourseProgress.of(
                        course,
                        requester.getUuid(),
                        course.getTotalLessonCount() == null ? 0 : course.getTotalLessonCount(),
                        0
                ));
        courseProgress.updateTotalLessonCount(course.getTotalLessonCount() == null ? 0 : course.getTotalLessonCount());
        courseProgress.updateCompletedLessonCount(completedCount);
        courseProgressRepository.save(courseProgress);

        xpRewardEventPublisher.publishXpRewardEvent(
                XpRewardEvent.of(
                        requester.getUuid().toString(),
                        lesson.getTitle() + " 수강 완료" ,
                        100L
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LessonDto.LessonDetailResponse getLessonDetail(Long lessonId, Requester requester) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new DomainException(GlobalErrorCode.NOT_FOUND));

        String lessonUserIdKey = LessonComplete.generateLessonUserIdKey(lessonId, requester.getUuid());
        boolean completed = lessonCompleteRepository.existsByLessonUserIdKey(lessonUserIdKey);
        String content = lesson.getContent() == null ? null : lesson.getContent().getContent();

        return LessonDto.LessonDetailResponse.from(lesson, content, completed);
    }

    private CourseDto.MyCourseResponse toMyCourseResponse(Course course, Requester requester) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByIdAsc(course.getId());
        List<Long> completedLessonIds = lessonCompleteRepository.findLessonIdsByUserIdAndCourseId(
                requester.getUuid(),
                course.getId()
        );
        Set<Long> completedLessonIdSet = new HashSet<>(completedLessonIds);

        List<LessonDto.LessonSummary> lessonSummaries = lessons.stream()
                .map(lesson -> LessonDto.LessonSummary.from(lesson, completedLessonIdSet.contains(lesson.getId())))
                .toList();

        return CourseDto.MyCourseResponse.from(course, lessonSummaries);
    }
}
