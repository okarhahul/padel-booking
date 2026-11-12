package com.padelarena.padel_boking.service;

import static com.padelarena.padel_boking.service.AvailabilityService.FIRST_HOUR;
import static com.padelarena.padel_boking.service.AvailabilityService.LAST_HOUR;
import static com.padelarena.padel_boking.service.AvailabilityService.MAX_DAYS;
import static com.padelarena.padel_boking.service.AvailabilityService.ZONE;

import com.padelarena.padel_boking.dto.BookingResponse;
import com.padelarena.padel_boking.dto.CreateBookingRequest;
import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingSlot;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.model.Court;
import com.padelarena.padel_boking.model.User;
import com.padelarena.padel_boking.repository.BookingRepository;
import com.padelarena.padel_boking.repository.BookingSlotRepository;
import com.padelarena.padel_boking.repository.CourtRepository;
import com.padelarena.padel_boking.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

    public static final int FEE = 2500;          // biaya layanan
    public static final int HOLD_MINUTES = 10;   // lama slot ditahan
    public static final int MAX_DURATION = 3;    // maksimal jam per pesanan

    private final BookingRepository bookingRepository;
    private final BookingSlotRepository slotRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingSlotRepository slotRepository,
                          CourtRepository courtRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.courtRepository = courtRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponse createHold(Long userId, CreateBookingRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Court court = courtRepository.findById(req.courtId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lapangan tidak ditemukan"));

        LocalDate today = LocalDate.now(ZONE);
        LocalDateTime now = LocalDateTime.now(ZONE);
        LocalDate date = req.playDate();
        int start = req.startHour();
        int duration = req.durationHours();
        int last = start + duration - 1;

        if (date.isBefore(today) || date.isAfter(today.plusDays(MAX_DAYS))) {
            throw bad("Tanggal di luar rentang booking (hari ini sampai " + MAX_DAYS + " hari ke depan)");
        }
        if (start < FIRST_HOUR || last > LAST_HOUR) {
            throw bad("Jam di luar jam operasional (07:00 - 22:00)");
        }
        if (date.equals(today) && start <= now.getHour()) {
            throw bad("Jam tersebut sudah lewat");
        }

        // Bersihkan hold lama yang kedaluwarsa supaya jamnya bisa dipesan lagi
        bookingRepository.deleteExpired(BookingStatus.HOLD, now);

        int subtotal = 0;
        for (int h = start; h <= last; h++) {
            subtotal += AvailabilityService.priceFor(h);
        }

        Booking booking = new Booking(user, court, date, start, duration, subtotal + FEE);
        booking.holdUntil(now.plusMinutes(HOLD_MINUTES));
        bookingRepository.save(booking);

        try {
            for (int h = start; h <= last; h++) {
                slotRepository.saveAndFlush(new BookingSlot(booking, court, date, h));
            }
        } catch (DataIntegrityViolationException e) {
            // Aturan UNIQUE di database menolak: jam itu sudah diambil orang lain.
            // Melempar error membatalkan seluruh transaksi, termasuk baris booking tadi.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jam yang dipilih sudah dipesan orang lain");
        }

        return BookingResponse.from(booking);
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}