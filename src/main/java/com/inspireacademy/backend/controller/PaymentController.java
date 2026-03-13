package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.model.Payment;
import com.inspireacademy.backend.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{reservationId}/checkout-session")
    public Map<String, String> createCheckoutSession(@PathVariable Long reservationId) throws Exception{
        String url = paymentService.createStripeCheckoutSession(reservationId);
        return Map.of("url", url);
    }
}