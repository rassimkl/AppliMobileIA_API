package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.dto.tests.*;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.UserRepository;
import com.inspireacademy.backend.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final UserRepository userRepository;

    private User getConnectedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 0) GET ALL TESTS (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminTestResponse> getAllTests() {
        return testService.getAllTestsForAdmin();
    }

    // 1) CREATE TEST (ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AdminTestResponse createTest(@RequestBody CreateTestRequest request,
                                        Authentication authentication) {
        User admin = getConnectedUser(authentication);
        return testService.createTest(request, admin);
    }

    // 2) ADD QUESTION (ADMIN)
    @PostMapping("/{testId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminQuestionResponse addQuestion(@PathVariable Long testId,
                                             @RequestBody CreateQuestionRequest request) {
        return testService.addQuestion(testId, request);
    }

    // 2bis) PUBLISH / UNPUBLISH (ADMIN)
    @PatchMapping("/{testId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminTestResponse publish(@PathVariable Long testId,
                                     @RequestBody PublishTestRequest request) {
        return testService.setPublished(testId, request.isPublished());
    }

    // 3) ASSIGN TEST TO STUDENT (ADMIN)
    @PostMapping("/{testId}/assign/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public TestAssignmentResponse assignTest(@PathVariable Long testId,
                                             @PathVariable Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return testService.assignTest(testId, student);
    }

    // 4) GET MY ASSIGNED TESTS (ETUDIANT)
    @GetMapping("/my-tests")
    @PreAuthorize("hasRole('ETUDIANT')")
    public List<TestAssignmentResponse> getMyTests(Authentication authentication) {
        User student = getConnectedUser(authentication);
        return testService.getAssignedTests(student);
    }

    // 5) GET TEST DETAILS (ETUDIANT)
    @GetMapping("/{testId}")
    @PreAuthorize("hasRole('ETUDIANT')")
    public TestDetailsResponse getTestDetails(@PathVariable Long testId,
                                              Authentication authentication) {
        User student = getConnectedUser(authentication);
        return testService.getTestDetails(testId, student);
    }

    // 6) SUBMIT TEST (ETUDIANT) -> DTO
    @PostMapping("/{testId}/submit")
    @PreAuthorize("hasRole('ETUDIANT')")
    public SubmitTestResponse submitTest(@PathVariable Long testId,
                                         @RequestBody SubmitTestRequest request,
                                         Authentication authentication) {
        User student = getConnectedUser(authentication);
        return testService.submitTest(testId, student, request);
    }

    // 7) GET RESULTS BY TEST (ADMIN / ENSEIGNANT) -> DTO
    @GetMapping("/{testId}/results")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public List<AdminTestResultResponse> getResults(@PathVariable Long testId) {
        return testService.getResultsByTest(testId);
    }

    // 8) GET TEST DETAILS (ADMIN) -> DTO
    @GetMapping("/{testId}/admin-details")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminTestDetailsResponse getAdminTestDetails(@PathVariable Long testId) {
        return testService.getAdminTestDetails(testId);
    }

    @DeleteMapping("/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTest(@PathVariable Long testId) {
        testService.deleteTest(testId);
    }

    @PutMapping("/{testId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminTestResponse updateTest(@PathVariable Long testId,
                                        @RequestBody CreateTestRequest request) {
        return testService.updateTest(testId, request);
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteQuestion(@PathVariable Long questionId) {
        testService.deleteQuestion(questionId);
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminQuestionResponse updateQuestion(@PathVariable Long questionId,
                                                @RequestBody CreateQuestionRequest request) {
        return testService.updateQuestion(questionId, request);
    }
}