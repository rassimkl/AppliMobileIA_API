package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.TrainingAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingAnswerRepository extends JpaRepository<TrainingAnswer, Long> {
}