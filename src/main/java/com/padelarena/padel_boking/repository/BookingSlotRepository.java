package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.BookingSlot;
import com.padelarena.padel_boking.model.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    // "Semua slot terisi di lapangan ini pada tanggal ini"
    List<BookingSlot> findByCourtIdAndPlayDate(Long courtId, LocalDate playDate);

    // Lepaskan semua slot milik satu pesanan (jam jadi kosong lagi)
    @Modifying(flushAutomatically = true)
    @Query("delete from BookingSlot s where s.booking.id = :bookingId")
    void releaseSlots(@Param("bookingId") Long bookingId);

    // Lepaskan slot milik semua pesanan yang waktunya sudah habis
    @Modifying(flushAutomatically = true)
    @Query("delete from BookingSlot s where s.booking.id in "
            + "(select b.id from Booking b where b.status in :statuses and b.expiresAt < :now)")
    void releaseExpiredSlots(@Param("statuses") Collection<BookingStatus> statuses,
                             @Param("now") LocalDateTime now);
}