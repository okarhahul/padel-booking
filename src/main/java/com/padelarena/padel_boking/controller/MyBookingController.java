package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.dto.BookingResponse;
import com.padelarena.padel_boking.dto.PageResponse;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.service.MyBookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class MyBookingController {

    private final MyBookingService myBookingService;

    public MyBookingController(MyBookingService myBookingService) {
        this.myBookingService = myBookingService;
    }

    @GetMapping("/mine")
    public PageResponse<BookingResponse> mine(@AuthenticationPrincipal Jwt jwt,
                                              @RequestParam(required = false) BookingStatus status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return myBookingService.list(userId(jwt), status, page, size);
    }

    @GetMapping("/{id}")
    public BookingResponse detail(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return myBookingService.get(userId(jwt), id);
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return myBookingService.cancel(userId(jwt), id);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}