package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.training.*;
import com.inspireacademy.backend.model.Langue;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.model.training.*;
import com.inspireacademy.backend.repository.LangueRepository;
import com.inspireacademy.backend.repository.UserRepository;
import com.inspireacademy.backend.repository.training.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingQuestionRepository trainingQuestionRepository;
    private final TrainingOptionRepository trainingOptionRepository;
    private final TrainingAssignmentRepository trainingAssignmentRepository;
    private final TrainingAttemptRepository trainingAttemptRepository;
    private final TrainingAnswerRepository trainingAnswerRepository;
    private final TrainingProgressRepository trainingProgressRepository;

    private final LangueRepository langueRepository;
    private final UserRepository userRepository;

    // =====================================================
    // CREATE TRAINING (ADMIN / ENSEIGNANT)
    // =====================================================
    @Transactional
    public AdminTrainingResponse createTraining(CreateTrainingRequest request, User creator) {
        validateCreateTrainingRequest(request);

        Langue langue = langueRepository.findById(request.getLangueId())
                .orElseThrow(() -> new RuntimeException("Langue not found"));

        boolean isAdmin = hasRole(creator, "ADMIN");
        boolean isTeacher = hasRole(creator, "ENSEIGNANT");

        if (!isAdmin && !isTeacher) {
            throw new RuntimeException("Only admin or teacher can create a training");
        }

        if (isTeacher && !teacherHasLanguage(creator, langue.getId())) {
            throw new RuntimeException("Teacher cannot create training for this language");
        }

        if (isTeacher && request.getVisibility() == TrainingVisibility.LANGUAGE_PUBLIC) {
            throw new RuntimeException("Teacher cannot create LANGUAGE_PUBLIC trainings");
        }

        Training training = Training.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .langue(langue)
                .createdBy(creator)
                .type(request.getType())
                .visibility(
                        request.getVisibility() != null
                                ? request.getVisibility()
                                : TrainingVisibility.ASSIGNED_ONLY
                )
                .published(false)
                .durationSeconds(request.getDurationSeconds())
                .fileUrl(request.getFileUrl())
                .coverUrl(request.getCoverUrl())
                .level(request.getLevel())
                .documentCategory(request.getDocumentCategory())
                .customCategory(request.getCustomCategory())
                .build();

        normalizeTrainingByType(training);

        return mapAdminResponse(trainingRepository.save(training));
    }

    // =====================================================
    // GET ALL TRAININGS (ADMIN)
    // =====================================================
    public List<AdminTrainingResponse> getAllTrainingsForAdmin() {
        return trainingRepository.findAll().stream()
                .map(this::mapAdminResponse)
                .toList();
    }

    // =====================================================
    // GET TEACHER TRAININGS
    // =====================================================
    public List<AdminTrainingResponse> getTeacherTrainings(User teacher) {
        ensureTeacher(teacher);

        return trainingRepository.findByCreatedById(teacher.getId()).stream()
                .map(this::mapAdminResponse)
                .toList();
    }

    // =====================================================
    // GET TEACHER TRAININGS BY LANGUE
    // =====================================================
    public List<AdminTrainingResponse> getTeacherTrainingsByLangue(User teacher, Long langueId) {
        ensureTeacher(teacher);

        if (!teacherHasLanguage(teacher, langueId)) {
            throw new RuntimeException("Teacher cannot access this language");
        }

        return trainingRepository.findByCreatedByIdAndLangueId(teacher.getId(), langueId).stream()
                .map(this::mapAdminResponse)
                .toList();
    }

    // =====================================================
    // UPDATE TRAINING (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @Transactional
    public AdminTrainingResponse updateTraining(Long trainingId, CreateTrainingRequest request, User currentUser) {
        validateCreateTrainingRequest(request);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        ensureCanManageTraining(currentUser, training);

        Langue langue = langueRepository.findById(request.getLangueId())
                .orElseThrow(() -> new RuntimeException("Langue not found"));

        boolean isTeacher = hasRole(currentUser, "ENSEIGNANT");

        if (isTeacher && !teacherHasLanguage(currentUser, langue.getId())) {
            throw new RuntimeException("Teacher cannot move training to this language");
        }

        if (isTeacher && request.getVisibility() == TrainingVisibility.LANGUAGE_PUBLIC) {
            throw new RuntimeException("Teacher cannot use LANGUAGE_PUBLIC visibility");
        }

        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setLangue(langue);
        training.setType(request.getType());
        training.setVisibility(
                request.getVisibility() != null
                        ? request.getVisibility()
                        : TrainingVisibility.ASSIGNED_ONLY
        );
        training.setDurationSeconds(request.getDurationSeconds());
        training.setFileUrl(request.getFileUrl());
        training.setCoverUrl(request.getCoverUrl());
        training.setLevel(request.getLevel());
        training.setDocumentCategory(request.getDocumentCategory());
        training.setCustomCategory(request.getCustomCategory());

        normalizeTrainingByType(training);

        return mapAdminResponse(trainingRepository.save(training));
    }

    // =====================================================
    // DELETE TRAINING (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @Transactional
    public void deleteTraining(Long trainingId, User currentUser) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        ensureCanManageTraining(currentUser, training);

        trainingRepository.delete(training);
    }

    // =====================================================
    // ADD QUESTION (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @Transactional
    public AdminTrainingQuestionResponse addQuestion(
            Long trainingId,
            CreateTrainingQuestionRequest request,
            User currentUser
    ) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        ensureCanManageTraining(currentUser, training);

        if (training.getType() != TrainingType.EXERCISE) {
            throw new RuntimeException("Questions can only be added to EXERCISE trainings");
        }

        validateQuestionRequest(request);

        Integer nextPosition = training.getQuestions() == null
                ? 1
                : training.getQuestions().size() + 1;

        TrainingQuestion question = TrainingQuestion.builder()
                .training(training)
                .position(nextPosition)
                .content(request.getContent())
                .type(request.getType())
                .points(request.getPoints() != null ? request.getPoints() : 0)
                .expectedAnswer(request.getExpectedAnswer())
                .caseSensitive(Boolean.TRUE.equals(request.getCaseSensitive()))
                .build();

        applyOptionsToQuestion(question, request);

        question = trainingQuestionRepository.save(question);

        return mapAdminQuestion(question);
    }

    // =====================================================
    // UPDATE QUESTION (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @Transactional
    public AdminTrainingQuestionResponse updateQuestion(
            Long questionId,
            CreateTrainingQuestionRequest request,
            User currentUser
    ) {
        TrainingQuestion question = trainingQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        ensureCanManageTraining(currentUser, question.getTraining());

        validateQuestionRequest(request);

        question.setContent(request.getContent());
        question.setType(request.getType());
        question.setPoints(request.getPoints() != null ? request.getPoints() : 0);
        question.setExpectedAnswer(request.getExpectedAnswer());
        question.setCaseSensitive(Boolean.TRUE.equals(request.getCaseSensitive()));

        question.getOptions().clear();
        applyOptionsToQuestion(question, request);

        question = trainingQuestionRepository.save(question);

        return mapAdminQuestion(question);
    }

    // =====================================================
    // DELETE QUESTION (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @Transactional
    public void deleteQuestion(Long questionId, User currentUser) {
        TrainingQuestion question = trainingQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        ensureCanManageTraining(currentUser, question.getTraining());

        trainingQuestionRepository.delete(question);
    }

    // =====================================================
    // PUBLISH / UNPUBLISH (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @Transactional
    public AdminTrainingResponse setPublished(Long trainingId, boolean published, User currentUser) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        ensureCanManageTraining(currentUser, training);

        if (published) {
            validateReadyToPublish(training);
        }

        training.setPublished(published);

        return mapAdminResponse(trainingRepository.save(training));
    }

    // =====================================================
    // ASSIGN TRAINING TO STUDENT (ADMIN / ENSEIGNANT)
    // =====================================================
    @Transactional
    public TrainingAssignmentResponse assignTraining(Long trainingId, Long studentId, User assignedBy) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!training.isPublished()) {
            throw new RuntimeException("Cannot assign unpublished training");
        }

        if (!hasRole(student, "ETUDIANT")) {
            throw new RuntimeException("Target user is not a student");
        }

        if (hasRole(assignedBy, "ENSEIGNANT")) {
            ensureTeacher(assignedBy);

            if (student.getTeacher() == null || !student.getTeacher().getId().equals(assignedBy.getId())) {
                throw new RuntimeException("Teacher can only assign to own students");
            }

            if (!teacherHasLanguage(assignedBy, training.getLangue().getId())) {
                throw new RuntimeException("Teacher cannot assign training outside own languages");
            }

            if (!training.getCreatedBy().getId().equals(assignedBy.getId())
                    && !hasRole(training.getCreatedBy(), "ADMIN")) {
                throw new RuntimeException("Teacher can only assign own trainings or admin trainings");
            }
        } else if (!hasRole(assignedBy, "ADMIN")) {
            throw new RuntimeException("Only admin or teacher can assign trainings");
        }

        trainingAssignmentRepository.findByTrainingIdAndStudentId(trainingId, studentId)
                .ifPresent(a -> {
                    throw new RuntimeException("Training already assigned to this student");
                });

        TrainingAssignment assignment = TrainingAssignment.builder()
                .training(training)
                .student(student)
                .assignedBy(assignedBy)
                .completed(false)
                .mandatory(true)
                .assignedAt(LocalDateTime.now())
                .build();

        assignment = trainingAssignmentRepository.save(assignment);

        return mapAssignmentResponse(assignment, null, null);
    }

    // =====================================================
    // GET STUDENT ASSIGNMENTS
    // =====================================================
    public List<TrainingAssignmentResponse> getStudentAssignments(User student) {
        ensureStudent(student);

        return trainingAssignmentRepository.findByStudentId(student.getId()).stream()
                .map(assignment -> {
                    Training training = assignment.getTraining();

                    TrainingAttempt lastAttempt = null;
                    TrainingProgress progress = null;

                    if (training.getType() == TrainingType.EXERCISE) {
                        lastAttempt = trainingAttemptRepository
                                .findTopByTrainingIdAndStudentIdOrderByCompletedAtDesc(training.getId(), student.getId())
                                .orElse(null);
                    } else if (training.getType() == TrainingType.DOCUMENT) {
                        progress = trainingProgressRepository
                                .findByTrainingIdAndStudentId(training.getId(), student.getId())
                                .orElse(null);
                    }

                    return mapAssignmentResponse(assignment, lastAttempt, progress);
                })
                .toList();
    }

    // =====================================================
    // GET STUDENT LIBRARY
    // =====================================================
    public List<TrainingResponse> getStudentLibrary(User student) {
        ensureStudent(student);

        Set<Long> studentLanguageIds = getStudentLanguageIds(student);

        if (studentLanguageIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Training> uniqueTrainings = new LinkedHashMap<>();

        for (Long langueId : studentLanguageIds) {
            List<Training> trainings = trainingRepository.findByLangueIdAndPublishedTrueAndVisibility(
                    langueId,
                    TrainingVisibility.LANGUAGE_PUBLIC
            );

            for (Training training : trainings) {
                if (training.getCreatedBy() != null && hasRole(training.getCreatedBy(), "ADMIN")) {
                    uniqueTrainings.put(training.getId(), training);
                }
            }
        }

        return uniqueTrainings.values().stream()
                .map(this::mapResponse)
                .toList();
    }

    // =====================================================
    // GET TRAINING DETAILS (STUDENT)
    // =====================================================
    public TrainingDetailsResponse getStudentTrainingDetails(Long trainingId, User student) {
        ensureStudent(student);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (!training.isPublished()) {
            throw new RuntimeException("Training not published");
        }

        boolean assigned = trainingAssignmentRepository
                .findByTrainingIdAndStudentId(trainingId, student.getId())
                .isPresent();

        boolean languagePublic = training.getVisibility() == TrainingVisibility.LANGUAGE_PUBLIC
                && training.getCreatedBy() != null
                && hasRole(training.getCreatedBy(), "ADMIN")
                && studentHasLanguage(student, training.getLangue().getId());

        if (!assigned && !languagePublic) {
            throw new RuntimeException("Access denied");
        }

        return mapStudentDetails(training);
    }

    // =====================================================
    // GET TRAINING DETAILS (ADMIN / ENSEIGNANT owner)
    // =====================================================
    public AdminTrainingDetailsResponse getAdminTrainingDetails(Long trainingId, User currentUser) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        ensureCanManageTraining(currentUser, training);

        return mapAdminDetails(training);
    }

    // =====================================================
    // SUBMIT TRAINING (EXERCISE)
    // =====================================================
    @Transactional
    public SubmitTrainingResponse submitTraining(Long trainingId, User student, SubmitTrainingRequest request) {
        ensureStudent(student);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (!training.isPublished()) {
            throw new RuntimeException("Training not published");
        }

        if (training.getType() != TrainingType.EXERCISE) {
            throw new RuntimeException("Only EXERCISE can be submitted");
        }

        trainingAssignmentRepository.findByTrainingIdAndStudentId(trainingId, student.getId())
                .orElseThrow(() -> new RuntimeException("Training not assigned to this student"));

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Answers required");
        }

        Set<Long> uniqueQ = new HashSet<>();
        for (TrainingAnswerRequest a : request.getAnswers()) {
            if (a.getQuestionId() == null) {
                throw new RuntimeException("questionId is required");
            }
            if (!uniqueQ.add(a.getQuestionId())) {
                throw new RuntimeException("Duplicate answer for questionId=" + a.getQuestionId());
            }
        }

        Set<Long> trainingQuestionIds = training.getQuestions().stream()
                .map(TrainingQuestion::getId)
                .collect(Collectors.toSet());

        if (!uniqueQ.equals(trainingQuestionIds)) {
            throw new RuntimeException("You must answer all questions of the training");
        }

        int maxScore = training.getQuestions().stream()
                .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 0)
                .sum();

        int nextAttemptNumber = trainingAttemptRepository
                .findTopByTrainingIdAndStudentIdOrderByAttemptNumberDesc(trainingId, student.getId())
                .map(a -> a.getAttemptNumber() + 1)
                .orElse(1);

        TrainingAttempt attempt = TrainingAttempt.builder()
                .training(training)
                .student(student)
                .attemptNumber(nextAttemptNumber)
                .score(0)
                .maxScore(maxScore)
                .completedAt(LocalDateTime.now())
                .build();

        attempt = trainingAttemptRepository.save(attempt);

        int totalScore = 0;

        for (TrainingAnswerRequest answerRequest : request.getAnswers()) {
            if (!trainingQuestionIds.contains(answerRequest.getQuestionId())) {
                throw new RuntimeException("Question does not belong to this training");
            }

            TrainingQuestion question = trainingQuestionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            int awardedPoints = 0;

            // QCM
            if (question.getType() == TrainingQuestionType.QCM) {
                if (answerRequest.getSelectedOptionId() == null) {
                    throw new RuntimeException("Option must be selected for QCM");
                }

                TrainingOption option = trainingOptionRepository.findById(answerRequest.getSelectedOptionId())
                        .orElseThrow(() -> new RuntimeException("Option not found"));

                if (!option.getQuestion().getId().equals(question.getId())) {
                    throw new RuntimeException("Option does not belong to this question");
                }

                if (option.isCorrect()) {
                    awardedPoints = question.getPoints() != null ? question.getPoints() : 0;
                }

                trainingAnswerRepository.save(
                        TrainingAnswer.builder()
                                .trainingAttempt(attempt)
                                .question(question)
                                .selectedOption(option)
                                .awardedPoints(awardedPoints)
                                .build()
                );
            }

            // TEXT
            if (question.getType() == TrainingQuestionType.TEXT) {
                String expected = question.getExpectedAnswer();
                String given = answerRequest.getTextAnswer();

                if (expected != null && given != null) {
                    String exp = expected;
                    String g = given;

                    if (!question.isCaseSensitive()) {
                        exp = exp.toLowerCase();
                        g = g.toLowerCase();
                    }

                    if (exp.trim().equals(g.trim())) {
                        awardedPoints = question.getPoints() != null ? question.getPoints() : 0;
                    }
                }

                trainingAnswerRepository.save(
                        TrainingAnswer.builder()
                                .trainingAttempt(attempt)
                                .question(question)
                                .textAnswer(given)
                                .awardedPoints(awardedPoints)
                                .build()
                );
            }

            // ORDER_WORDS / ORDER_SENTENCE
            if (question.getType() == TrainingQuestionType.ORDER_WORDS
                    || question.getType() == TrainingQuestionType.ORDER_SENTENCE) {

                List<Long> givenOrder = answerRequest.getOrderedOptionIds();

                if (givenOrder == null || givenOrder.isEmpty()) {
                    throw new RuntimeException("orderedOptionIds is required for order question");
                }

                List<Long> expectedOrder = buildExpectedOrderedOptionIds(question);

                Set<Long> expectedSet = new HashSet<>(expectedOrder);
                Set<Long> givenSet = new HashSet<>(givenOrder);

                if (givenOrder.size() != expectedOrder.size() || !givenSet.equals(expectedSet)) {
                    throw new RuntimeException("Invalid orderedOptionIds for this question");
                }

                if (givenOrder.equals(expectedOrder)) {
                    awardedPoints = question.getPoints() != null ? question.getPoints() : 0;
                }

                trainingAnswerRepository.save(
                        TrainingAnswer.builder()
                                .trainingAttempt(attempt)
                                .question(question)
                                .textAnswer(buildOrderedOptionText(givenOrder, question))
                                .awardedPoints(awardedPoints)
                                .build()
                );
            }

            totalScore += awardedPoints;
        }

        attempt.setScore(totalScore);
        trainingAttemptRepository.save(attempt);

        TrainingAssignment assignment = trainingAssignmentRepository
                .findByTrainingIdAndStudentId(trainingId, student.getId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setCompleted(true);
        trainingAssignmentRepository.save(assignment);

        return SubmitTrainingResponse.builder()
                .attemptId(attempt.getId())
                .score(attempt.getScore())
                .maxScore(attempt.getMaxScore())
                .completedAt(attempt.getCompletedAt())
                .build();
    }

    // =====================================================
    // SAVE / UPDATE PROGRESS (DOCUMENT)
    // =====================================================
    @Transactional
    public TrainingProgressResponse saveProgress(Long trainingId, User student, Integer progressPercent) {
        ensureStudent(student);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (!training.isPublished()) {
            throw new RuntimeException("Training not published");
        }

        if (training.getType() != TrainingType.DOCUMENT) {
            throw new RuntimeException("Progress tracking only available for DOCUMENT");
        }

        boolean assigned = trainingAssignmentRepository
                .findByTrainingIdAndStudentId(trainingId, student.getId())
                .isPresent();

        boolean languagePublic = training.getVisibility() == TrainingVisibility.LANGUAGE_PUBLIC
                && training.getCreatedBy() != null
                && hasRole(training.getCreatedBy(), "ADMIN")
                && studentHasLanguage(student, training.getLangue().getId());

        if (!assigned && !languagePublic) {
            throw new RuntimeException("Access denied");
        }

        int normalized = progressPercent == null ? 0 : Math.max(0, Math.min(100, progressPercent));

        TrainingProgress progress = trainingProgressRepository
                .findByTrainingIdAndStudentId(trainingId, student.getId())
                .orElse(
                        TrainingProgress.builder()
                                .training(training)
                                .student(student)
                                .progressPercent(0)
                                .completed(false)
                                .build()
                );

        progress.setProgressPercent(normalized);
        progress.setCompleted(normalized >= 100);
        progress.setLastOpenedAt(LocalDateTime.now());

        progress = trainingProgressRepository.save(progress);

        TrainingAssignment assignment = trainingAssignmentRepository
                .findByTrainingIdAndStudentId(trainingId, student.getId())
                .orElse(null);

        if (assignment != null && progress.isCompleted()) {
            assignment.setCompleted(true);
            trainingAssignmentRepository.save(assignment);
        }

        return TrainingProgressResponse.builder()
                .trainingId(training.getId())
                .title(training.getTitle())
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .type(training.getType())
                .progressPercent(progress.getProgressPercent())
                .completed(progress.isCompleted())
                .lastOpenedAt(progress.getLastOpenedAt())
                .build();
    }

    // =====================================================
    // GET STUDENT PROGRESS
    // =====================================================
    public List<TrainingProgressResponse> getStudentProgress(User student) {
        ensureStudent(student);

        return trainingProgressRepository.findByStudentId(student.getId()).stream()
                .map(progress -> TrainingProgressResponse.builder()
                        .trainingId(progress.getTraining().getId())
                        .title(progress.getTraining().getTitle())
                        .langue(progress.getTraining().getLangue() != null
                                ? progress.getTraining().getLangue().getName()
                                : null)
                        .type(progress.getTraining().getType())
                        .progressPercent(progress.getProgressPercent())
                        .completed(progress.isCompleted())
                        .lastOpenedAt(progress.getLastOpenedAt())
                        .build())
                .toList();
    }

    // =====================================================
    // GET RESULTS BY TRAINING (ADMIN / ENSEIGNANT)
    // =====================================================
    public List<TrainingResultResponse> getResultsByTraining(Long trainingId, User currentUser) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (hasRole(currentUser, "ADMIN")) {
            return trainingAttemptRepository.findByTrainingId(trainingId).stream()
                    .map(this::mapResultResponse)
                    .toList();
        }

        if (hasRole(currentUser, "ENSEIGNANT")) {
            ensureTeacher(currentUser);

            if (!training.getCreatedBy().getId().equals(currentUser.getId())
                    && !hasRole(training.getCreatedBy(), "ADMIN")) {
                throw new RuntimeException("Teacher cannot access results of this training");
            }

            return trainingAttemptRepository.findByTrainingId(trainingId).stream()
                    .filter(attempt ->
                            attempt.getStudent().getTeacher() != null
                                    && attempt.getStudent().getTeacher().getId().equals(currentUser.getId()))
                    .map(this::mapResultResponse)
                    .toList();
        }

        throw new RuntimeException("Access denied");
    }

    // =====================================================
    // GET RESULT DETAILS (ADMIN / ENSEIGNANT)
    // =====================================================
    public TrainingResultDetailsResponse getResultDetails(Long attemptId, User currentUser) {
        TrainingAttempt attempt = trainingAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (hasRole(currentUser, "ADMIN")) {
            return mapResultDetails(attempt);
        }

        if (hasRole(currentUser, "ENSEIGNANT")) {
            ensureTeacher(currentUser);

            if (attempt.getStudent().getTeacher() == null
                    || !attempt.getStudent().getTeacher().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied");
            }

            return mapResultDetails(attempt);
        }

        throw new RuntimeException("Access denied");
    }

    // =====================================================
    // GET STUDENT OWN RESULT DETAILS
    // =====================================================
    public TrainingResultDetailsResponse getStudentResultDetails(Long attemptId, User student) {
        ensureStudent(student);

        TrainingAttempt attempt = trainingAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapResultDetails(attempt);
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void validateCreateTrainingRequest(CreateTrainingRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Title is required");
        }

        if (request.getLangueId() == null) {
            throw new RuntimeException("langueId is required");
        }

        if (request.getType() == null) {
            throw new RuntimeException("type is required");
        }

        if (request.getType() == TrainingType.EXERCISE) {
            if (request.getDurationSeconds() == null || request.getDurationSeconds() <= 0) {
                throw new RuntimeException("durationSeconds must be > 0 for EXERCISE");
            }
        }

        if (request.getType() == TrainingType.DOCUMENT) {
            if (request.getFileUrl() == null || request.getFileUrl().isBlank()) {
                throw new RuntimeException("fileUrl is required for DOCUMENT");
            }

            if (request.getDocumentCategory() == null) {
                throw new RuntimeException("documentCategory is required for DOCUMENT");
            }

            if (request.getDocumentCategory() == DocumentCategory.OTHER) {
                if (request.getCustomCategory() == null || request.getCustomCategory().isBlank()) {
                    throw new RuntimeException("customCategory is required when documentCategory = OTHER");
                }
            }
        }
    }

    private void normalizeTrainingByType(Training training) {
        if (training.getType() == TrainingType.EXERCISE) {
            training.setFileUrl(null);
            training.setDocumentCategory(null);
            training.setCustomCategory(null);
            return;
        }

        if (training.getType() == TrainingType.DOCUMENT) {
            training.setDurationSeconds(null);

            if (training.getDocumentCategory() != DocumentCategory.OTHER) {
                training.setCustomCategory(null);
            }

            if (training.getQuestions() != null) {
                training.getQuestions().clear();
            }
        }
    }

    private void validateReadyToPublish(Training training) {
        if (training.getType() == TrainingType.EXERCISE) {
            if (training.getQuestions() == null || training.getQuestions().isEmpty()) {
                throw new RuntimeException("Cannot publish an EXERCISE without questions");
            }
        }

        if (training.getType() == TrainingType.DOCUMENT) {
            if (training.getFileUrl() == null || training.getFileUrl().isBlank()) {
                throw new RuntimeException("Cannot publish DOCUMENT without fileUrl");
            }

            if (training.getDocumentCategory() == null) {
                throw new RuntimeException("Cannot publish DOCUMENT without documentCategory");
            }

            if (training.getDocumentCategory() == DocumentCategory.OTHER
                    && (training.getCustomCategory() == null || training.getCustomCategory().isBlank())) {
                throw new RuntimeException("Cannot publish DOCUMENT with OTHER category without customCategory");
            }
        }
    }

    private void validateQuestionRequest(CreateTrainingQuestionRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new RuntimeException("Question content is required");
        }

        if (request.getType() == null) {
            throw new RuntimeException("Question type is required");
        }

        if (request.getType() == TrainingQuestionType.QCM) {
            if (request.getOptions() == null || request.getOptions().size() < 2) {
                throw new RuntimeException("QCM must contain at least 2 options");
            }

            long correctCount = request.getOptions().stream()
                    .filter(o -> Boolean.TRUE.equals(o.getCorrect()))
                    .count();

            if (correctCount != 1) {
                throw new RuntimeException("QCM must contain exactly one correct option");
            }
        }

        if (request.getType() == TrainingQuestionType.TEXT) {
            if (request.getExpectedAnswer() == null || request.getExpectedAnswer().isBlank()) {
                throw new RuntimeException("expectedAnswer is required for TEXT");
            }
        }

        if (request.getType() == TrainingQuestionType.ORDER_WORDS
                || request.getType() == TrainingQuestionType.ORDER_SENTENCE) {

            if (request.getOptions() == null || request.getOptions().size() < 2) {
                throw new RuntimeException("Order question must contain at least 2 items");
            }

            Set<Integer> positions = new HashSet<>();

            for (TrainingOptionRequest option : request.getOptions()) {
                if (option.getContent() == null || option.getContent().isBlank()) {
                    throw new RuntimeException("Each order item must contain content");
                }

                if (option.getPosition() == null || option.getPosition() <= 0) {
                    throw new RuntimeException("Each order item must contain a valid position");
                }

                if (!positions.add(option.getPosition())) {
                    throw new RuntimeException("Duplicate position in order question");
                }
            }

            for (int i = 1; i <= request.getOptions().size(); i++) {
                if (!positions.contains(i)) {
                    throw new RuntimeException("Order positions must be continuous from 1 to " + request.getOptions().size());
                }
            }
        }
    }

    private void applyOptionsToQuestion(TrainingQuestion question, CreateTrainingQuestionRequest request) {
        question.setExpectedAnswer(null);
        question.setCaseSensitive(false);

        if (request.getType() == TrainingQuestionType.TEXT) {
            question.setExpectedAnswer(request.getExpectedAnswer());
            question.setCaseSensitive(Boolean.TRUE.equals(request.getCaseSensitive()));
            return;
        }

        if (request.getType() == TrainingQuestionType.QCM) {
            int index = 1;
            for (TrainingOptionRequest opt : request.getOptions()) {
                if (opt.getContent() == null || opt.getContent().isBlank()) {
                    continue;
                }

                TrainingOption option = TrainingOption.builder()
                        .question(question)
                        .content(opt.getContent().trim())
                        .correct(Boolean.TRUE.equals(opt.getCorrect()))
                        .position(index++)
                        .build();

                question.getOptions().add(option);
            }

            return;
        }

        if (request.getType() == TrainingQuestionType.ORDER_WORDS
                || request.getType() == TrainingQuestionType.ORDER_SENTENCE) {

            for (TrainingOptionRequest opt : request.getOptions()) {
                TrainingOption option = TrainingOption.builder()
                        .question(question)
                        .content(opt.getContent().trim())
                        .correct(false)
                        .position(opt.getPosition())
                        .build();

                question.getOptions().add(option);
            }
        }
    }

    private boolean hasRole(User user, String roleName) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.getRole().name().equalsIgnoreCase(roleName);
    }

    private void ensureStudent(User user) {
        if (!hasRole(user, "ETUDIANT")) {
            throw new RuntimeException("User is not a student");
        }
    }

    private void ensureTeacher(User user) {
        if (!hasRole(user, "ENSEIGNANT")) {
            throw new RuntimeException("User is not a teacher");
        }
    }

    private void ensureCanManageTraining(User currentUser, Training training) {
        if (hasRole(currentUser, "ADMIN")) {
            return;
        }

        if (hasRole(currentUser, "ENSEIGNANT")
                && training.getCreatedBy() != null
                && training.getCreatedBy().getId().equals(currentUser.getId())) {
            return;
        }

        throw new RuntimeException("Access denied");
    }

    private boolean teacherHasLanguage(User teacher, Long langueId) {
        if (teacher == null || teacher.getLanguages() == null) {
            return false;
        }

        return teacher.getLanguages().stream()
                .anyMatch(langue -> langue.getId().equals(langueId));
    }

    private Set<Long> getStudentLanguageIds(User student) {
        if (student == null || student.getLanguages() == null || student.getLanguages().isEmpty()) {
            return Set.of();
        }

        return student.getLanguages().stream()
                .map(Langue::getId)
                .collect(Collectors.toSet());
    }

    private boolean studentHasLanguage(User student, Long langueId) {
        return getStudentLanguageIds(student).contains(langueId);
    }

    private List<Long> buildExpectedOrderedOptionIds(TrainingQuestion question) {
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            return List.of();
        }

        return question.getOptions().stream()
                .sorted(Comparator.comparing(TrainingOption::getPosition))
                .map(TrainingOption::getId)
                .toList();
    }

    private String buildOrderedOptionText(List<Long> orderedOptionIds, TrainingQuestion question) {
        if (orderedOptionIds == null || orderedOptionIds.isEmpty()) {
            return "";
        }

        Map<Long, String> optionTextById = question.getOptions().stream()
                .collect(Collectors.toMap(TrainingOption::getId, TrainingOption::getContent));

        return orderedOptionIds.stream()
                .map(optionTextById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" | "));
    }

    private String buildExpectedOrderAnswer(TrainingQuestion question) {
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            return "";
        }

        return question.getOptions().stream()
                .sorted(Comparator.comparing(TrainingOption::getPosition))
                .map(TrainingOption::getContent)
                .collect(Collectors.joining(" | "));
    }

    private AdminTrainingResponse mapAdminResponse(Training training) {
        return AdminTrainingResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .langueId(training.getLangue() != null ? training.getLangue().getId() : null)
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .type(training.getType())
                .visibility(training.getVisibility())
                .published(training.isPublished())
                .durationSeconds(training.getDurationSeconds())
                .fileUrl(training.getFileUrl())
                .coverUrl(training.getCoverUrl())
                .level(training.getLevel())
                .documentCategory(training.getDocumentCategory())
                .customCategory(training.getCustomCategory())
                .createdById(training.getCreatedBy() != null ? training.getCreatedBy().getId() : null)
                .createdByEmail(training.getCreatedBy() != null ? training.getCreatedBy().getEmail() : null)
                .createdAt(training.getCreatedAt())
                .updatedAt(training.getUpdatedAt())
                .build();
    }

    private TrainingResponse mapResponse(Training training) {
        return TrainingResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .langueId(training.getLangue() != null ? training.getLangue().getId() : null)
                .type(training.getType())
                .visibility(training.getVisibility())
                .published(training.isPublished())
                .durationSeconds(training.getDurationSeconds())
                .fileUrl(training.getFileUrl())
                .coverUrl(training.getCoverUrl())
                .level(training.getLevel())
                .documentCategory(training.getDocumentCategory())
                .customCategory(training.getCustomCategory())
                .createdById(training.getCreatedBy() != null ? training.getCreatedBy().getId() : null)
                .createdByEmail(training.getCreatedBy() != null ? training.getCreatedBy().getEmail() : null)
                .createdAt(training.getCreatedAt())
                .updatedAt(training.getUpdatedAt())
                .build();
    }

    private AdminTrainingQuestionResponse mapAdminQuestion(TrainingQuestion question) {
        List<AdminTrainingOptionResponse> options = null;

        if (question.getType() == TrainingQuestionType.QCM
                || question.getType() == TrainingQuestionType.ORDER_WORDS
                || question.getType() == TrainingQuestionType.ORDER_SENTENCE) {

            options = question.getOptions().stream()
                    .sorted(Comparator.comparing(
                            o -> o.getPosition() != null ? o.getPosition() : Integer.MAX_VALUE
                    ))
                    .map(o -> AdminTrainingOptionResponse.builder()
                            .id(o.getId())
                            .content(o.getContent())
                            .correct(o.isCorrect())
                            .position(o.getPosition())
                            .build())
                    .toList();
        }

        return AdminTrainingQuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .type(question.getType().name())
                .points(question.getPoints())
                .expectedAnswer(question.getExpectedAnswer())
                .caseSensitive(question.isCaseSensitive())
                .options(options)
                .build();
    }

    private TrainingAssignmentResponse mapAssignmentResponse(
            TrainingAssignment assignment,
            TrainingAttempt attempt,
            TrainingProgress progress
    ) {
        return TrainingAssignmentResponse.builder()
                .trainingId(assignment.getTraining().getId())
                .title(assignment.getTraining().getTitle())
                .langue(assignment.getTraining().getLangue() != null
                        ? assignment.getTraining().getLangue().getName()
                        : null)
                .type(assignment.getTraining().getType())
                .completed(assignment.isCompleted())
                .mandatory(assignment.isMandatory())
                .score(attempt != null ? attempt.getScore() : null)
                .maxScore(attempt != null ? attempt.getMaxScore() : null)
                .progressPercent(progress != null ? progress.getProgressPercent() : null)
                .attemptId(attempt != null ? attempt.getId() : null)
                .assignedAt(assignment.getAssignedAt())
                .dueDate(assignment.getDueDate())
                .build();
    }

    private TrainingDetailsResponse mapStudentDetails(Training training) {
        return TrainingDetailsResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .type(training.getType())
                .durationSeconds(training.getDurationSeconds())
                .fileUrl(training.getFileUrl())
                .coverUrl(training.getCoverUrl())
                .level(training.getLevel())
                .documentCategory(training.getDocumentCategory())
                .customCategory(training.getCustomCategory())
                .questions(
                        training.getQuestions() != null
                                ? training.getQuestions().stream()
                                .map(q -> TrainingQuestionResponse.builder()
                                        .id(q.getId())
                                        .content(q.getContent())
                                        .type(q.getType())
                                        .points(q.getPoints())
                                        .options(
                                                q.getType() == TrainingQuestionType.TEXT
                                                        ? null
                                                        : q.getOptions().stream()
                                                        .map(o -> TrainingOptionResponse.builder()
                                                                .id(o.getId())
                                                                .content(o.getContent())
                                                                .position(null)
                                                                .build())
                                                        .toList()
                                        )
                                        .build())
                                .toList()
                                : List.of()
                )
                .build();
    }

    private AdminTrainingDetailsResponse mapAdminDetails(Training training) {
        return AdminTrainingDetailsResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .langueId(training.getLangue() != null ? training.getLangue().getId() : null)
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .type(training.getType())
                .visibility(training.getVisibility())
                .published(training.isPublished())
                .durationSeconds(training.getDurationSeconds())
                .fileUrl(training.getFileUrl())
                .coverUrl(training.getCoverUrl())
                .level(training.getLevel())
                .documentCategory(training.getDocumentCategory())
                .customCategory(training.getCustomCategory())
                .questions(
                        training.getQuestions() != null
                                ? training.getQuestions().stream()
                                .map(this::mapAdminQuestion)
                                .toList()
                                : List.of()
                )
                .build();
    }

    private TrainingResultResponse mapResultResponse(TrainingAttempt attempt) {
        return TrainingResultResponse.builder()
                .attemptId(attempt.getId())
                .studentId(attempt.getStudent().getId())
                .studentEmail(attempt.getStudent().getEmail())
                .studentFirstName(attempt.getStudent().getFirstName())
                .studentLastName(attempt.getStudent().getLastName())
                .score(attempt.getScore())
                .maxScore(attempt.getMaxScore())
                .completedAt(attempt.getCompletedAt())
                .build();
    }

    private TrainingResultDetailsResponse mapResultDetails(TrainingAttempt attempt) {
        List<TrainingAnswerDetailsResponse> answers = attempt.getAnswers().stream()
                .map(answer -> {
                    TrainingQuestion q = answer.getQuestion();

                    List<AdminTrainingOptionResponse> options = null;

                    if (q.getType() != TrainingQuestionType.TEXT) {
                        options = q.getOptions().stream()
                                .sorted(Comparator.comparing(
                                        o -> o.getPosition() != null ? o.getPosition() : Integer.MAX_VALUE
                                ))
                                .map(o -> AdminTrainingOptionResponse.builder()
                                        .id(o.getId())
                                        .content(o.getContent())
                                        .correct(o.isCorrect())
                                        .position(o.getPosition())
                                        .build())
                                .toList();
                    }

                    String studentAnswer = null;
                    String correctAnswer = null;

                    if (q.getType() == TrainingQuestionType.QCM) {
                        studentAnswer = answer.getSelectedOption() != null
                                ? answer.getSelectedOption().getContent()
                                : null;

                        correctAnswer = q.getOptions().stream()
                                .filter(TrainingOption::isCorrect)
                                .findFirst()
                                .map(TrainingOption::getContent)
                                .orElse(null);
                    }

                    if (q.getType() == TrainingQuestionType.TEXT) {
                        studentAnswer = answer.getTextAnswer();
                        correctAnswer = q.getExpectedAnswer();
                    }

                    if (q.getType() == TrainingQuestionType.ORDER_WORDS
                            || q.getType() == TrainingQuestionType.ORDER_SENTENCE) {
                        studentAnswer = answer.getTextAnswer();
                        correctAnswer = buildExpectedOrderAnswer(q);
                    }

                    return TrainingAnswerDetailsResponse.builder()
                            .questionId(q.getId())
                            .questionContent(q.getContent())
                            .questionType(q.getType().name())
                            .questionPoints(q.getPoints())
                            .studentAnswer(studentAnswer)
                            .correctAnswer(correctAnswer)
                            .awardedPoints(answer.getAwardedPoints())
                            .options(options)
                            .build();
                })
                .toList();

        return TrainingResultDetailsResponse.builder()
                .attemptId(attempt.getId())
                .studentEmail(attempt.getStudent().getEmail())
                .studentFirstName(attempt.getStudent().getFirstName())
                .studentLastName(attempt.getStudent().getLastName())
                .score(attempt.getScore())
                .maxScore(attempt.getMaxScore())
                .completedAt(attempt.getCompletedAt())
                .answers(answers)
                .build();
    }
}