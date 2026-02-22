package com.inspireacademy.backend.repository;

import com.inspireacademy.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Pour récupérer les réservations d’un utilisateur
    List<Reservation> findByUserId(Long userId);

    // Pour récupérer les réservations d’un plan
    List<Reservation> findByPlanId(Long planId);
}
