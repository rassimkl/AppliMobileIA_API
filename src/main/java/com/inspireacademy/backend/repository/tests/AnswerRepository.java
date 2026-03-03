package com.inspireacademy.backend.repository.tests;

import com.inspireacademy.backend.model.tests.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}