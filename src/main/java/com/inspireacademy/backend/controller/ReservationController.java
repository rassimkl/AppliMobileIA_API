package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.dto.ReservationResponse;
import com.inspireacademy.backend.model.Reservation;
import com.inspireacademy.backend.model.ReservationStatus;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.PaymentRepository;
import com.inspireacademy.backend.repository.UserRepository;
import com.inspireacademy.backend.service.ReservationService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public ReservationController(
            ReservationService reservationService,
            UserRepository userRepository,
            PaymentRepository paymentRepository
    ) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    // ===============================
    // CREATE RESERVATION
    // ===============================
    @PostMapping("/{planId}")
    public Reservation createReservation(
            @PathVariable Long planId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return reservationService.createReservation(planId, userEmail);
    }

    // ===============================
    // GET MY RESERVATIONS (ETUDIANT)
    // ===============================
    @GetMapping("/my")
    public List<ReservationResponse> getMyReservations(Authentication authentication) {

        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reservationService.getReservationsByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===============================
    // APPROVE RESERVATION (ADMIN)
    // ===============================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public Reservation approveReservation(@PathVariable Long id) {
        return reservationService.updateReservationStatus(id, ReservationStatus.APPROVED);
    }

    // ===============================
    // REJECT RESERVATION (ADMIN)
    // ===============================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public Reservation rejectReservation(@PathVariable Long id) {
        return reservationService.updateReservationStatus(id, ReservationStatus.REJECTED);
    }

    // ===============================
    // GET ALL RESERVATIONS (ADMIN)
    // ===============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ReservationResponse> getAllReservations() {

        return reservationService.getAllReservations()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ===============================
    // PRIVATE MAPPER METHOD
    // ===============================
    private ReservationResponse mapToResponse(Reservation reservation) {

        boolean paid = paymentRepository
                .findByReservationId(reservation.getId())
                .map(p -> p.getStatus().equals("PAID"))
                .orElse(false);

        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getUser().getEmail(),
                reservation.getPlan().getCourse().getTitle(),
                reservation.getPlan().getPrice(),
                reservation.getCreatedAt(),
                paid
        );
    }
}