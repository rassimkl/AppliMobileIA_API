package com.inspireacademy.backend.service;

import com.inspireacademy.backend.model.Payment;
import com.inspireacademy.backend.model.Reservation;
import com.inspireacademy.backend.model.ReservationStatus;
import com.inspireacademy.backend.repository.PaymentRepository;
import com.inspireacademy.backend.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public Payment processPayment(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new RuntimeException("Reservation not approved");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(reservation.getPlan().getPrice());
        payment.setStatus("PAID");
        payment.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}