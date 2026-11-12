package com.padelarena.padel_boking.dto;

import com.padelarena.padel_boking.model.Booking;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long courtId,
        String courtName,
        LocalDate playDate,
        int startHour,
        int durationHours,
        String status,
        int totalPrice,
        LocalDateTime expiresAt) {

    public static BookingResponse from(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getCourt().getId(),
                b.getCourt().getName(),
                b.getPlayDate(),
                b.getStartHour(),
                b.getDurationHours(),
                b.getStatus().name(),
                b.getTotalPrice(),
                b.getExpiresAt());
    }
}