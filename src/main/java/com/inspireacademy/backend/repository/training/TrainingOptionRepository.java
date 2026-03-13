package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.TrainingOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingOptionRepository extends JpaRepository<TrainingOption, Long> {

    List<TrainingOption> findByQuestionId(Long questionId);
}