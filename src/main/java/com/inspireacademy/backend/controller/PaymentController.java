package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.model.Payment;
import com.inspireacademy.backend.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{reservationId}")
    public Payment payReservation(@PathVariable Long reservationId) {
        return paymentService.processPayment(reservationId);
    }
}