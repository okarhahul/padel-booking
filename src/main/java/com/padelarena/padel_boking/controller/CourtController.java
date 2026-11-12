package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.dto.SlotResponse;
import com.padelarena.padel_boking.model.Court;
import com.padelarena.padel_boking.repository.CourtRepository;
import com.padelarena.padel_boking.service.AvailabilityService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    private final CourtRepository courtRepository;
    private final AvailabilityService availabilityService;

    public CourtController(CourtRepository courtRepository, AvailabilityService availabilityService) {
        this.courtRepository = courtRepository;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public List<Court> getAll() {
        return courtRepository.findAll();
    }

    @GetMapping("/{courtId}/availability")
    public List<SlotResponse> availability(
            @PathVariable Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.getAvailability(courtId, date);
    }
}