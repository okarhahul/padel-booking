package com.padelarena.padel_boking.controller;

import com.padelarena.padel_boking.dto.AdminTransactionResponse;
import com.padelarena.padel_boking.dto.PageResponse;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.service.AdminTransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionController {

    private final AdminTransactionService service;

    public AdminTransactionController(AdminTransactionService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<AdminTransactionResponse> list(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page) {
        return service.list(status, page);
    }
}