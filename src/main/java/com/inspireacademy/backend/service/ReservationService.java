package com.inspireacademy.backend.service;

import com.inspireacademy.backend.model.Plan;
import com.inspireacademy.backend.model.Reservation;
import com.inspireacademy.backend.model.ReservationStatus;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.PlanRepository;
import com.inspireacademy.backend.repository.ReservationRepository;
import com.inspireacademy.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            PlanRepository planRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    // ==========================
    // CREATE RESERVATION
    // ==========================
    public Reservation createReservation(Long planId, String userEmail) {

        Plan plan = planRepository.findById(planId)
                .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new RuntimeException("Plan not found");
                    }
                });

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
                    @Override
                    public RuntimeException get() {
                        return new RuntimeException("User not found");
                    }
                });

        List<Reservation> existingReservations =
                reservationRepository.findByUserId(user.getId());

        for (Reservation r : existingReservations) {
            if (r.getPlan().getId().equals(planId)) {
                throw new RuntimeException("You already requested this plan");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setPlan(plan);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    // ==========================
    // UPDATE STATUS (ADMIN)
    // ==========================
    public Reservation updateReservationStatus(Long reservationId, ReservationStatus newStatus) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(newStatus);

        return reservationRepository.save(reservation);
    }

    // ==========================
    // GET BY USER
    // ==========================
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // ==========================
    // GET ALL
    // ==========================
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}
