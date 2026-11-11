package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.model.Court;
import com.padelarena.padel_boking.repository.CourtRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    private final CourtRepository courtRepository;

    public CourtController(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    @GetMapping
    public List<Court> getAll() {
        return courtRepository.findAll();
    }
}