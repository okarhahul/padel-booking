package com.padelarena.padel_boking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull Long courtId,
        @NotNull LocalDate playDate,
        @NotNull @Min(7) @Max(21) Integer startHour,
        @NotNull @Min(1) @Max(3) Integer durationHours) {
}