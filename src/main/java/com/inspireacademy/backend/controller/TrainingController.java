package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.dto.training.*;
import com.inspireacademy.backend.model.Role;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.UserRepository;
import com.inspireacademy.backend.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final UserRepository userRepository;

    private User getConnectedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =====================================================
    // LIST TRAININGS
    // ADMIN -> tous les trainings
    // ENSEIGNANT -> ses trainings
    // =====================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public List<AdminTrainingResponse> getTrainings(
            Authentication authentication,
            @RequestParam(required = false) Long langueId
    ) {
        User currentUser = getConnectedUser(authentication);

        if (currentUser.getRole() == Role.ADMIN) {
            return trainingService.getAllTrainingsForAdmin();
        }

        if (currentUser.getRole() == Role.ENSEIGNANT) {
            if (langueId != null) {
                return trainingService.getTeacherTrainingsByLangue(currentUser, langueId);
            }
            return trainingService.getTeacherTrainings(currentUser);
        }

        throw new RuntimeException("Access denied");
    }

    // =====================================================
    // CREATE TRAINING (ADMIN / ENSEIGNANT)
    // =====================================================
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public AdminTrainingResponse createTraining(
            @RequestBody CreateTrainingRequest request,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.createTraining(request, currentUser);
    }

    // =====================================================
    // UPDATE TRAINING (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @PutMapping("/{trainingId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public AdminTrainingResponse updateTraining(
            @PathVariable Long trainingId,
            @RequestBody CreateTrainingRequest request,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.updateTraining(trainingId, request, currentUser);
    }

    // =====================================================
    // DELETE TRAINING (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @DeleteMapping("/{trainingId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public void deleteTraining(
            @PathVariable Long trainingId,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        trainingService.deleteTraining(trainingId, currentUser);
    }

    // =====================================================
    // ADMIN / TEACHER DETAILS
    // =====================================================
    @GetMapping("/{trainingId}/admin-details")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public AdminTrainingDetailsResponse getAdminTrainingDetails(
            @PathVariable Long trainingId,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.getAdminTrainingDetails(trainingId, currentUser);
    }

    // =====================================================
    // PUBLISH / UNPUBLISH (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @PatchMapping("/{trainingId}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public AdminTrainingResponse publishTraining(
            @PathVariable Long trainingId,
            @RequestBody PublishTrainingRequest request,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.setPublished(trainingId, request.isPublished(), currentUser);
    }

    // =====================================================
    // ADD QUESTION (ADMIN / ENSEIGNANT owner)
    // uniquement pour EXERCISE
    // =====================================================
    @PostMapping("/{trainingId}/questions")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public AdminTrainingQuestionResponse addQuestion(
            @PathVariable Long trainingId,
            @RequestBody CreateTrainingQuestionRequest request,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.addQuestion(trainingId, request, currentUser);
    }

    // =====================================================
    // UPDATE QUESTION (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public AdminTrainingQuestionResponse updateQuestion(
            @PathVariable Long questionId,
            @RequestBody CreateTrainingQuestionRequest request,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.updateQuestion(questionId, request, currentUser);
    }

    // =====================================================
    // DELETE QUESTION (ADMIN / ENSEIGNANT owner)
    // =====================================================
    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public void deleteQuestion(
            @PathVariable Long questionId,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        trainingService.deleteQuestion(questionId, currentUser);
    }

    // =====================================================
    // ASSIGN TRAINING TO STUDENT (ADMIN / ENSEIGNANT)
    // =====================================================
    @PostMapping("/{trainingId}/assign/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public TrainingAssignmentResponse assignTraining(
            @PathVariable Long trainingId,
            @PathVariable Long studentId,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.assignTraining(trainingId, studentId, currentUser);
    }

    // =====================================================
    // STUDENT - MY ASSIGNMENTS
    // =====================================================
    @GetMapping("/my-assignments")
    @PreAuthorize("hasRole('ETUDIANT')")
    public List<TrainingAssignmentResponse> getMyAssignments(Authentication authentication) {
        User student = getConnectedUser(authentication);
        return trainingService.getStudentAssignments(student);
    }

    // =====================================================
    // STUDENT - MY LIBRARY
    // contenus admin publics liés à ses langues
    // =====================================================
    @GetMapping("/my-library")
    @PreAuthorize("hasRole('ETUDIANT')")
    public List<TrainingResponse> getMyLibrary(Authentication authentication) {
        User student = getConnectedUser(authentication);
        return trainingService.getStudentLibrary(student);
    }

    // =====================================================
    // STUDENT - TRAINING DETAILS
    // =====================================================
    @GetMapping("/{trainingId}")
    @PreAuthorize("hasRole('ETUDIANT')")
    public TrainingDetailsResponse getStudentTrainingDetails(
            @PathVariable Long trainingId,
            Authentication authentication
    ) {
        User student = getConnectedUser(authentication);
        return trainingService.getStudentTrainingDetails(trainingId, student);
    }

    // =====================================================
    // STUDENT - SUBMIT EXERCISE
    // =====================================================
    @PostMapping("/{trainingId}/submit")
    @PreAuthorize("hasRole('ETUDIANT')")
    public SubmitTrainingResponse submitTraining(
            @PathVariable Long trainingId,
            @RequestBody SubmitTrainingRequest request,
            Authentication authentication
    ) {
        User student = getConnectedUser(authentication);
        return trainingService.submitTraining(trainingId, student, request);
    }

    // =====================================================
    // STUDENT - SAVE PROGRESS (DOCUMENT)
    // =====================================================
    @PostMapping("/{trainingId}/progress")
    @PreAuthorize("hasRole('ETUDIANT')")
    public TrainingProgressResponse saveProgress(
            @PathVariable Long trainingId,
            @RequestBody SaveTrainingProgressRequest request,
            Authentication authentication
    ) {
        User student = getConnectedUser(authentication);
        return trainingService.saveProgress(trainingId, student, request.getProgressPercent());
    }

    // =====================================================
    // STUDENT - GET MY PROGRESS
    // =====================================================
    @GetMapping("/my-progress")
    @PreAuthorize("hasRole('ETUDIANT')")
    public List<TrainingProgressResponse> getMyProgress(Authentication authentication) {
        User student = getConnectedUser(authentication);
        return trainingService.getStudentProgress(student);
    }

    // =====================================================
    // RESULTS BY TRAINING (ADMIN / ENSEIGNANT)
    // =====================================================
    @GetMapping("/{trainingId}/results")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public List<TrainingResultResponse> getResultsByTraining(
            @PathVariable Long trainingId,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.getResultsByTraining(trainingId, currentUser);
    }

    // =====================================================
    // RESULT DETAILS (ADMIN / ENSEIGNANT)
    // =====================================================
    @GetMapping("/results/{attemptId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public TrainingResultDetailsResponse getResultDetails(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {
        User currentUser = getConnectedUser(authentication);
        return trainingService.getResultDetails(attemptId, currentUser);
    }

    // =====================================================
    // STUDENT - MY RESULT DETAILS
    // =====================================================
    @GetMapping("/my-results/{attemptId}")
    @PreAuthorize("hasRole('ETUDIANT')")
    public TrainingResultDetailsResponse getMyResultDetails(
            @PathVariable Long attemptId,
            Authentication authentication
    ) {
        User student = getConnectedUser(authentication);
        return trainingService.getStudentResultDetails(attemptId, student);
    }
}