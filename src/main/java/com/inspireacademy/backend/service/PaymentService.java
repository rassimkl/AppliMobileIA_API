package com.inspireacademy.backend.service;

import com.inspireacademy.backend.repository.PaymentRepository;
import com.inspireacademy.backend.repository.ReservationRepository;

import com.inspireacademy.backend.model.Payment;
import com.inspireacademy.backend.model.Reservation;
import com.inspireacademy.backend.model.ReservationStatus;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Value("${app.frontendSuccessUrl}")
    private String successUrl;

    @Value("${app.frontendCancelUrl}")
    private String cancelUrl;

    public PaymentService(PaymentRepository paymentRepository,
                          ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public String createStripeCheckoutSession(Long reservationId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new RuntimeException("Reservation not approved");
        }

        var opt = paymentRepository.findByReservationId(reservationId);

        if (opt.isPresent()) {

            Payment existing = opt.get();

            if ("PAID".equals(existing.getStatus())) {
                throw new RuntimeException("Reservation already paid");
            }

            if ("PENDING".equals(existing.getStatus())) {

                Session session = Session.retrieve(existing.getCheckoutSessionId());

                if ("open".equals(session.getStatus())) {

                    return session.getUrl();

                } else {

                    paymentRepository.delete(existing);
                }
            }

            if ("FAILED".equals(existing.getStatus())) {
                paymentRepository.delete(existing);
            }
        }

        // ⚠️ IMPORTANT: prix calculé côté serveur
        long amountInCents = Math.round(reservation.getPlan().getPrice() * 100.0);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("eur")
                                                        .setUnitAmount(amountInCents)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Réservation " + reservation.getPlan().getCourse().getTitle())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        // metadata => retrouver la réservation dans le webhook
                        .putMetadata("reservationId", reservation.getId().toString())
                        .build();

        try {
            Session session = Session.create(params);

            // Enregistre un payment "PENDING" (optionnel mais recommandé)
            Payment payment = new Payment();
            payment.setReservation(reservation);
            payment.setAmount(reservation.getPlan().getPrice());
            payment.setCurrency("eur");
            payment.setProvider("STRIPE");
            payment.setStatus("PENDING");
            payment.setCheckoutSessionId(session.getId());
            payment.setCreatedAt(java.time.LocalDateTime.now());
            paymentRepository.save(payment);

            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe error: " + e.getMessage());
        }
    }


    @Transactional
    public void handleStripeEvent(Event event) throws Exception {

        // Empêche Stripe d'envoyer le même événement deux fois
        if (paymentRepository.existsByStripeEventId(event.getId())) {
            return;
        }

        // ✅ Paiement réussi
        if ("checkout.session.completed".equals(event.getType())) {

            Session session = (Session) ApiResource.GSON.fromJson(
                    event.getDataObjectDeserializer().getRawJson(), Session.class
            );

            String reservationIdStr = session.getMetadata().get("reservationId");
            Long reservationId = Long.valueOf(reservationIdStr);

            Payment payment = paymentRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setStatus("PAID");
            payment.setStripeEventId(event.getId());
            payment.setPaymentIntentId(session.getPaymentIntent());

            paymentRepository.save(payment);
        }

        // ❌ Paiement échoué
        if ("payment_intent.payment_failed".equals(event.getType())) {

            com.stripe.model.PaymentIntent intent =
                    (com.stripe.model.PaymentIntent) ApiResource.GSON.fromJson(
                            event.getDataObjectDeserializer().getRawJson(),
                            com.stripe.model.PaymentIntent.class
                    );

            String paymentIntentId = intent.getId();

            Payment payment = paymentRepository
                    .findByPaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setStatus("FAILED");
            payment.setStripeEventId(event.getId());

            paymentRepository.save(payment);
        }
    }
}