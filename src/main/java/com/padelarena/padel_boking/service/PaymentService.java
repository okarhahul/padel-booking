package com.padelarena.padel_boking.service;

import static com.padelarena.padel_boking.service.AvailabilityService.ZONE;

import com.padelarena.padel_boking.dto.BookingResponse;
import com.padelarena.padel_boking.dto.PayRequest;
import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.repository.BookingRepository;
import com.padelarena.padel_boking.repository.BookingSlotRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    public static final int PAYMENT_MINUTES = 10;   // batas waktu bayar setelah memilih metode
    public static final String PROMO_CODE = "PADEL10";
    public static final int PROMO_MAX_DISCOUNT = 50_000;

    private final BookingRepository bookingRepository;
    private final BookingSlotRepository slotRepository;

    public PaymentService(BookingRepository bookingRepository, BookingSlotRepository slotRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
    }

    // noRollbackFor: kalau pesanan ternyata sudah kedaluwarsa, perubahan "jadi EXPIRED + lepas slot"
    // harus tetap tersimpan walaupun kita membalas dengan error 410.
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public BookingResponse pay(Long userId, Long bookingId, PayRequest req) {
        Booking booking = findOwned(userId, bookingId);
        LocalDateTime now = LocalDateTime.now(ZONE);

        if (booking.getStatus() != BookingStatus.HOLD) {
            throw conflict("Pesanan ini tidak bisa dibayar, statusnya " + booking.getStatus());
        }
        if (booking.getExpiresAt().isBefore(now)) {
            expire(booking);
            throw new ResponseStatusException(HttpStatus.GONE, "Waktu tahan slot sudah habis");
        }

        // Harga di database = subtotal + biaya layanan. Biaya layanan dikurangi dulu untuk dapat subtotal.
        int subtotal = booking.getTotalPrice() - BookingService.FEE;
        int discount = 0;

        String code = req.promoCode() == null ? "" : req.promoCode().trim().toUpperCase();
        if (!code.isEmpty()) {
            if (!code.equals(PROMO_CODE)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kode promo tidak valid");
            }
            discount = Math.min((int) Math.round(subtotal * 0.1), PROMO_MAX_DISCOUNT);
        }

        int total = subtotal + BookingService.FEE - discount;
        booking.markPending(req.method(), total, now.plusMinutes(PAYMENT_MINUTES));
        return BookingResponse.from(booking);
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public BookingResponse simulate(Long userId, Long bookingId, boolean success) {
        Booking booking = findOwned(userId, bookingId);
        LocalDateTime now = LocalDateTime.now(ZONE);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw conflict("Pesanan ini tidak sedang menunggu pembayaran, statusnya " + booking.getStatus());
        }
        if (booking.getExpiresAt().isBefore(now)) {
            expire(booking);
            throw new ResponseStatusException(HttpStatus.GONE, "Waktu pembayaran sudah habis");
        }

        if (success) {
            booking.markPaid(now);
        } else {
            booking.markFailed();
            slotRepository.releaseSlots(booking.getId());   // jam kosong lagi
        }
        return BookingResponse.from(booking);
    }

    private void expire(Booking booking) {
        booking.markExpired();
        slotRepository.releaseSlots(booking.getId());
    }

    // Pesanan milik orang lain dijawab "tidak ditemukan" (404), bukan "dilarang" (403),
    // supaya orang tidak bisa menebak nomor pesanan milik orang lain.
    private Booking findOwned(Long userId, Long bookingId) {
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pesanan tidak ditemukan"));
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}