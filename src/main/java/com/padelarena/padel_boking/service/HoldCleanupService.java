package com.padelarena.padel_boking.service;

import com.padelarena.padel_boking.model.BookingStatus;
import com.padelarena.padel_boking.repository.BookingRepository;
import com.padelarena.padel_boking.repository.BookingSlotRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HoldCleanupService {

    // Hanya pesanan yang masih menunggu yang bisa kedaluwarsa. PAID tidak pernah.
    private static final List<BookingStatus> WAITING = List.of(BookingStatus.HOLD, BookingStatus.PENDING);

    private final BookingRepository bookingRepository;
    private final BookingSlotRepository slotRepository;

    public HoldCleanupService(BookingRepository bookingRepository, BookingSlotRepository slotRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
    }

    // Urutan penting: lepas slot dulu (selagi statusnya masih HOLD/PENDING), baru ubah status.
    @Transactional
    public int releaseExpired() {
        LocalDateTime now = LocalDateTime.now(AvailabilityService.ZONE);
        slotRepository.releaseExpiredSlots(WAITING, now);
        return bookingRepository.markExpired(WAITING, now);
    }
}