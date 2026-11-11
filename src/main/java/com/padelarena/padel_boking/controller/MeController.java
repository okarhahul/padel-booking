package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.dto.UserResponse;
import com.padelarena.padel_boking.model.User;
import com.padelarena.padel_boking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class MeController {

    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Siapa saya? Identitas diambil dari token, bukan dari yang dikirim user.
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        Long id = Long.valueOf(jwt.getSubject());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}