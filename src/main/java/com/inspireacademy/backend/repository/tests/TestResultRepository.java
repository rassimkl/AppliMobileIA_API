package com.inspireacademy.backend.repository.tests;

import com.inspireacademy.backend.model.tests.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    List<TestResult> findByStudentId(Long studentId);

    List<TestResult> findByTestId(Long testId);

    Optional<TestResult> findTopByTestIdAndStudentIdOrderByCompletedAtDesc(Long testId, Long studentId);

}