package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.config.JwtService;
import com.padelarena.padel_boking.dto.LoginRequest;
import com.padelarena.padel_boking.dto.LoginResponse;
import com.padelarena.padel_boking.dto.RegisterRequest;
import com.padelarena.padel_boking.dto.UserResponse;
import com.padelarena.padel_boking.model.User;
import com.padelarena.padel_boking.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah terdaftar");
        }

        User user = new User(
                request.name().trim(),
                email,
                request.whatsapp(),
                passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return toResponse(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Pesan yang sama untuk "email tidak ada" dan "password salah",
        // supaya orang tidak bisa menebak email mana yang terdaftar.
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email atau password salah"));

        return new LoginResponse(
                jwtService.createToken(user),
                "Bearer",
                jwtService.getExpirySeconds(),
                toResponse(user));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}