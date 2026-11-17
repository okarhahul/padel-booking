package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.BookingSlot;
import java.time.LocalDate;
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
}