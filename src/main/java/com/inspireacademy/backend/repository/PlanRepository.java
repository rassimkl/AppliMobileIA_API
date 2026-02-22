package com.inspireacademy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.inspireacademy.backend.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByCourse_Language(String language);

    List<Plan> findByNumberOfHours(Integer numberOfHours);

    List<Plan> findByCourse_LanguageAndNumberOfHours(String language, Integer numberOfHours);
}

