package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.tests.*;
import com.inspireacademy.backend.model.Langue;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.model.tests.*;
import com.inspireacademy.backend.repository.LangueRepository;
import com.inspireacademy.backend.repository.tests.*;
import com.inspireacademy.backend.service.mapper.AdminTestDetailsMapper;
import com.inspireacademy.backend.service.mapper.TestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final TestAssignmentRepository assignmentRepository;
    private final TestResultRepository resultRepository;
    private final AnswerRepository answerRepository;
    private final LangueRepository langueRepository;

    // =====================================================
    // 1) CREATE TEST (ADMIN) -> DTO
    // =====================================================
    public AdminTestResponse createTest(CreateTestRequest request, User admin) {
        Langue langue = langueRepository.findById(request.getLangueId())
                .orElseThrow(() -> new RuntimeException("Langue not found"));

        Test test = Test.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .langue(langue)
                .createdBy(admin)
                .durationSeconds(request.getDurationSeconds())
                .published(false)
                .build();

        return TestMapper.toAdminTestResponse(testRepository.save(test));
    }

    // =====================================================
    // 0) GET ALL TESTS (ADMIN) -> DTO
    // =====================================================
    public List<AdminTestResponse> getAllTestsForAdmin() {
        return testRepository.findAll().stream()
                .map(TestMapper::toAdminTestResponse)
                .toList();
    }

    // =====================================================
    // 2) ADD QUESTION (ADMIN) -> DTO
    // =====================================================
    @Transactional
    public AdminQuestionResponse addQuestion(Long testId, CreateQuestionRequest request) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        Question question = Question.builder()
                .test(test)
                .content(request.getContent())
                .type(request.getType())
                .points(request.getPoints())
                .expectedAnswer(request.getExpectedAnswer())
                .caseSensitive(Boolean.TRUE.equals(request.getCaseSensitive()))
                .build();

        // ⚠️ Important : on ajoute les options AVANT le save
        if (request.getType() == QuestionType.QCM) {

            if (request.getOptions() == null || request.getOptions().isEmpty()) {
                throw new RuntimeException("QCM must contain options");
            }

            for (OptionRequest opt : request.getOptions()) {

                Option option = Option.builder()
                        .question(question)
                        .content(opt.getContent())
                        .correct(Boolean.TRUE.equals(opt.getCorrect()))
                        .build();

                // ✅ On ajoute à la collection gérée par Hibernate
                question.getOptions().add(option);
            }
        }

        // 🔥 Un seul save suffit (cascade = ALL)
        question = questionRepository.save(question);

        // Mapping admin
        List<AdminOptionResponse> options = null;

        if (question.getType() == QuestionType.QCM) {
            options = question.getOptions().stream()
                    .map(o -> AdminOptionResponse.builder()
                            .id(o.getId())
                            .content(o.getContent())
                            .correct(o.isCorrect())
                            .build())
                    .toList();
        }

        return AdminQuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .type(question.getType().name())
                .points(question.getPoints())
                .options(options)
                .build();
    }

    // =====================================================
    // 2bis) PUBLISH / UNPUBLISH (ADMIN)
    // =====================================================
    @Transactional
    public AdminTestResponse setPublished(Long testId, boolean published) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        // Empêcher publish d'un test vide
        if (published && (test.getQuestions() == null || test.getQuestions().isEmpty())) {
            throw new RuntimeException("Cannot publish a test without questions");
        }

        test.setPublished(published);
        return TestMapper.toAdminTestResponse(testRepository.save(test));
    }

    // =====================================================
    // 3) ASSIGN TEST (ADMIN) -> DTO
    // =====================================================
    public TestAssignmentResponse assignTest(Long testId, User student) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.isPublished()) {
            throw new RuntimeException("Cannot assign an unpublished test");
        }

        assignmentRepository.findByTestIdAndStudentId(testId, student.getId())
                .ifPresent(a -> {
                    throw new RuntimeException("Test already assigned to this student");
                });

        TestAssignment assignment = TestAssignment.builder()
                .test(test)
                .student(student)
                .completed(false)
                .assignedAt(LocalDateTime.now())
                .build();

        assignment = assignmentRepository.save(assignment);

        // ✅ au moment de l’assignation, score/maxScore = null
        return TestAssignmentResponse.builder()
                .testId(assignment.getTest().getId())
                .title(assignment.getTest().getTitle())
                .langue(assignment.getTest().getLangue().getName())
                .durationSeconds(assignment.getTest().getDurationSeconds())
                .completed(false)
                .score(null)
                .maxScore(null)
                .build();
    }

    // =====================================================
    // 4) GET ASSIGNED TESTS (STUDENT) -> AVEC SCORE/MAXSCORE
    // =====================================================
    public List<TestAssignmentResponse> getAssignedTests(User student) {

        return assignmentRepository.findByStudentId(student.getId())
                .stream()
                .map(a -> {

                    // ✅ On récupère le dernier résultat si completed = true
                    TestResult lastResult = null;

                    if (a.isCompleted()) {
                        lastResult = resultRepository
                                .findTopByTestIdAndStudentIdOrderByCompletedAtDesc(
                                        a.getTest().getId(),
                                        student.getId()
                                )
                                .orElse(null);
                    }

                    return TestAssignmentResponse.builder()
                            .testId(a.getTest().getId())
                            .title(a.getTest().getTitle())
                            .langue(a.getTest().getLangue().getName())
                            .durationSeconds(a.getTest().getDurationSeconds())
                            .completed(a.isCompleted())
                            .score(lastResult != null ? lastResult.getScore() : null)
                            .maxScore(lastResult != null ? lastResult.getMaxScore() : null)
                            .build();
                })
                .toList();
    }

    // =====================================================
    // 5) GET TEST DETAILS (STUDENT) - secured DTO
    // =====================================================
    public TestDetailsResponse getTestDetails(Long testId, User student) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.isPublished()) {
            throw new RuntimeException("Test not published");
        }

        assignmentRepository.findByTestIdAndStudentId(testId, student.getId())
                .orElseThrow(() -> new RuntimeException("Access denied"));

        return TestDetailsResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .durationSeconds(test.getDurationSeconds())
                .questions(
                        test.getQuestions().stream()
                                .map(q -> QuestionResponse.builder()
                                        .id(q.getId())
                                        .content(q.getContent())
                                        .type(q.getType())
                                        .points(q.getPoints())
                                        .options(
                                                q.getType() == QuestionType.QCM
                                                        ? q.getOptions().stream()
                                                        .map(o -> OptionResponse.builder()
                                                                .id(o.getId())
                                                                .content(o.getContent())
                                                                .build())
                                                        .toList()
                                                        : null
                                        )
                                        .build())
                                .toList()
                )
                .build();
    }

    // =====================================================
    // 6) ADMIN DETAILS
    // =====================================================
    public AdminTestDetailsResponse getAdminTestDetails(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        return AdminTestDetailsMapper.toAdminDetails(test);
    }

    // =====================================================
    // 7) SUBMIT TEST -> DTO + validations solides
    // =====================================================
    @Transactional
    public SubmitTestResponse submitTest(Long testId, User student, SubmitTestRequest request) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (!test.isPublished()) {
            throw new RuntimeException("Test not published");
        }

        TestAssignment assignment = assignmentRepository
                .findByTestIdAndStudentId(testId, student.getId())
                .orElseThrow(() -> new RuntimeException("Test not assigned to this student"));

        if (assignment.isCompleted()) {
            throw new RuntimeException("Test already completed");
        }

        if (request.getAnswers() == null) {
            throw new RuntimeException("Answers required");
        }

        // ✅ Validation: pas de doublons questionId
        Set<Long> uniqueQ = new HashSet<>();
        for (AnswerRequest a : request.getAnswers()) {
            if (a.getQuestionId() == null) throw new RuntimeException("questionId is required");
            if (!uniqueQ.add(a.getQuestionId()))
                throw new RuntimeException("Duplicate answer for questionId=" + a.getQuestionId());
        }

        // ✅ Validation: toutes les questions du test doivent être répondues
        Set<Long> testQuestionIds = test.getQuestions().stream()
                .map(Question::getId)
                .collect(Collectors.toSet());

        if (!uniqueQ.equals(testQuestionIds)) {
            throw new RuntimeException("You must answer all questions of the test");
        }

        int maxScore = test.getQuestions().stream().mapToInt(Question::getPoints).sum();

        TestResult result = TestResult.builder()
                .test(test)
                .student(student)
                .attemptNumber(1)
                .score(0)
                .maxScore(maxScore)
                .completedAt(LocalDateTime.now())
                .build();

        result = resultRepository.save(result);

        int totalScore = 0;

        for (AnswerRequest answerRequest : request.getAnswers()) {

            // ✅ Validation: question appartient au test
            if (!testQuestionIds.contains(answerRequest.getQuestionId())) {
                throw new RuntimeException("Question does not belong to this test");
            }

            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            int awardedPoints = 0;

            if (question.getType() == QuestionType.QCM) {

                if (answerRequest.getSelectedOptionId() == null) {
                    throw new RuntimeException("Option must be selected for QCM");
                }

                Option option = optionRepository.findById(answerRequest.getSelectedOptionId())
                        .orElseThrow(() -> new RuntimeException("Option not found"));

                if (!option.getQuestion().getId().equals(question.getId())) {
                    throw new RuntimeException("Option does not belong to this question");
                }

                if (option.isCorrect()) awardedPoints = question.getPoints();

                answerRepository.save(Answer.builder()
                        .testResult(result)
                        .question(question)
                        .selectedOption(option)
                        .awardedPoints(awardedPoints)
                        .build());
            }

            if (question.getType() == QuestionType.TEXT) {

                String expected = question.getExpectedAnswer();
                String given = answerRequest.getTextAnswer();

                if (expected != null && given != null) {
                    String exp = expected;
                    String g = given;

                    if (!question.isCaseSensitive()) {
                        exp = exp.toLowerCase();
                        g = g.toLowerCase();
                    }

                    if (exp.trim().equals(g.trim())) awardedPoints = question.getPoints();
                }

                answerRepository.save(Answer.builder()
                        .testResult(result)
                        .question(question)
                        .textAnswer(given)
                        .awardedPoints(awardedPoints)
                        .build());
            }

            totalScore += awardedPoints;
        }

        result.setScore(totalScore);
        resultRepository.save(result);

        assignment.setCompleted(true);
        assignmentRepository.save(assignment);

        return SubmitTestResponse.builder()
                .resultId(result.getId())
                .score(result.getScore())
                .maxScore(result.getMaxScore())
                .completedAt(result.getCompletedAt())
                .build();
    }

    // =====================================================
    // 8) RESULTS (ADMIN/ENSEIGNANT) -> DTO
    // =====================================================
    public List<AdminTestResultResponse> getResultsByTest(Long testId) {
        return resultRepository.findByTestId(testId).stream()
                .map(r -> AdminTestResultResponse.builder()
                        .resultId(r.getId())
                        .studentId(r.getStudent().getId())
                        .studentEmail(r.getStudent().getEmail())
                        .score(r.getScore())
                        .maxScore(r.getMaxScore())
                        .completedAt(r.getCompletedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void deleteTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        testRepository.delete(test);
    }

    @Transactional
    public AdminTestResponse updateTest(Long testId, CreateTestRequest request) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setDurationSeconds(request.getDurationSeconds());

        return TestMapper.toAdminTestResponse(testRepository.save(test));
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        questionRepository.delete(q);
    }

    @Transactional
    public AdminQuestionResponse updateQuestion(Long questionId, CreateQuestionRequest request) {

        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        q.setContent(request.getContent());
        q.setPoints(request.getPoints());
        q.setExpectedAnswer(request.getExpectedAnswer());
        q.setCaseSensitive(Boolean.TRUE.equals(request.getCaseSensitive()));

        // ✅ On sauvegarde
        q = questionRepository.save(q);

        return AdminQuestionResponse.builder()
                .id(q.getId())
                .content(q.getContent())
                .type(q.getType().name())
                .points(q.getPoints())
                .build();
    }
}