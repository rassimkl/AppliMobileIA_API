package com.inspireacademy.backend.repository;

import com.inspireacademy.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Vérifier si un paiement existe déjà pour une réservation
    Optional<Payment> findByReservationId(Long reservationId);

}