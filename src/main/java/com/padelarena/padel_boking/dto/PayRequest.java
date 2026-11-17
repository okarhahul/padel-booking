package com.padelarena.padel_boking.dto;

import com.padelarena.padel_boking.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PayRequest(
        @NotNull PaymentMethod method,
        @Size(max = 20) String promoCode) {
}