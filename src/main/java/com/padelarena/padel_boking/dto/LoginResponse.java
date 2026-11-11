package com.padelarena.padel_boking.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user) {
}