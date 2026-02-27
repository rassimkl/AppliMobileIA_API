package com.inspireacademy.backend.repository;

import com.inspireacademy.backend.model.Langue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LangueRepository extends JpaRepository<Langue, Long> {
    boolean existsByNameIgnoreCase(String name);
}