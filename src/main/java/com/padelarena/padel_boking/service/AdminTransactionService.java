package com.padelarena.padel_boking.service;

import com.padelarena.padel_boking.dto.AdminTransactionResponse;
import com.padelarena.padel_boking.dto.PageResponse;
import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.repository.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminTransactionService {

    public static final int PAGE_SIZE = 15;   // dikunci di server

    private final BookingRepository bookingRepository;

    public AdminTransactionService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminTransactionResponse> list(BookingStatus status, int page) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<Booking> result = (status == null)
                ? bookingRepository.findAllBy(pageable)
                : bookingRepository.findByStatus(status, pageable);

        return PageResponse.from(result.map(AdminTransactionResponse::from));
    }
}