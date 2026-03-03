package com.inspireacademy.backend.repository.tests;

import com.inspireacademy.backend.model.tests.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findByPublishedTrue();

}