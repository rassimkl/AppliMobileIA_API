package com.inspireacademy.backend.repository.tests;

import com.inspireacademy.backend.model.tests.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

}