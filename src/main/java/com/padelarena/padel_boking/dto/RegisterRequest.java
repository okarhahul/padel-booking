package com.padelarena.padel_boking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Pattern(regexp = "^\\+?[0-9]{9,19}$", message = "Nomor WhatsApp tidak valid") String whatsapp,
        @NotBlank @Size(min = 8, max = 72) String password) {
}