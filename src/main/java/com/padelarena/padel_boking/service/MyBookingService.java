package com.padelarena.padel_boking.service;

import com.padelarena.padel_boking.dto.BookingResponse;
import com.padelarena.padel_boking.dto.PageResponse;
import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.repository.BookingRepository;
import com.padelarena.padel_boking.repository.BookingSlotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MyBookingService {

    private static final int MAX_PAGE_SIZE = 50;

    private final BookingRepository bookingRepository;
    private final BookingSlotRepository slotRepository;

    public MyBookingService(BookingRepository bookingRepository, BookingSlotRepository slotRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> list(Long userId, BookingStatus status, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Booking> result = (status == null)
                ? bookingRepository.findByUserId(userId, pageable)
                : bookingRepository.findByUserIdAndStatus(userId, status, pageable);

        return PageResponse.from(result.map(BookingResponse::from));
    }

    @Transactional(readOnly = true)
    public BookingResponse get(Long userId, Long bookingId) {
        return BookingResponse.from(findOwned(userId, bookingId));
    }

    @Transactional
    public BookingResponse cancel(Long userId, Long bookingId) {
        Booking booking = findOwned(userId, bookingId);

        if (booking.getStatus() != BookingStatus.HOLD && booking.getStatus() != BookingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pesanan dengan status " + booking.getStatus() + " tidak bisa dibatalkan");
        }

        booking.markCancelled();
        slotRepository.releaseSlots(booking.getId());   // jam kosong lagi
        return BookingResponse.from(booking);
    }

    // Milik orang lain dijawab 404, bukan 403, supaya nomor pesanan tidak bisa ditebak.
    private Booking findOwned(Long userId, Long bookingId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pesanan tidak ditemukan"));
    }
}