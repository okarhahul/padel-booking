package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.dto.BookingResponse;
import com.padelarena.padel_boking.dto.PayRequest;
import com.padelarena.padel_boking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings/{id}")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public BookingResponse pay(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id,
                               @Valid @RequestBody PayRequest request) {
        return paymentService.pay(Long.valueOf(jwt.getSubject()), id, request);
    }

    // Hanya untuk simulasi/demo: menggantikan notifikasi dari payment gateway.
    @PostMapping("/simulate-payment")
    public BookingResponse simulate(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable Long id,
                                    @RequestParam boolean success) {
        return paymentService.simulate(Long.valueOf(jwt.getSubject()), id, success);
    }
}