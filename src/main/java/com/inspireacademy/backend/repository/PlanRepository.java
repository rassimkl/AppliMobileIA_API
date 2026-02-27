package com.inspireacademy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.inspireacademy.backend.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByCourse_Langue_Name(String name);

    List<Plan> findByNumberOfHours(Integer numberOfHours);

    List<Plan> findByCourse_Langue_NameAndNumberOfHours(String name, Integer numberOfHours);
}