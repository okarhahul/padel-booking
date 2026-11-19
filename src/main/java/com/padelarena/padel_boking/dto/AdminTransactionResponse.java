package com.padelarena.padel_boking.dto;

import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.model.PaymentMethod;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminTransactionResponse(
        Long id,
        String customerName,
        String customerEmail,   // sudah disamarkan
        String courtName,
        LocalDate playDate,
        int startHour,
        int durationHours,
        BookingStatus status,
        PaymentMethod paymentMethod,
        int totalPrice,
        LocalDateTime paidAt) {

    public static AdminTransactionResponse from(Booking b) {
        return new AdminTransactionResponse(
                b.getId(),
                b.getUser().getName(),
                maskEmail(b.getUser().getEmail()),
                b.getCourt().getName(),
                b.getPlayDate(),
                b.getStartHour(),
                b.getDurationHours(),
                b.getStatus(),
                b.getPaymentMethod(),
                b.getTotalPrice(),
                b.getPaidAt());
    }

    // budi@padel.test -> b***@padel.test
    static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 0) return "***";
        return email.charAt(0) + "***" + email.substring(at);
    }
}