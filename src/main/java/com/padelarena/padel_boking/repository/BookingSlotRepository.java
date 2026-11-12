package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.BookingSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {

    // "Semua slot terisi di lapangan ini pada tanggal ini"
    List<BookingSlot> findByCourtIdAndPlayDate(Long courtId, LocalDate playDate);
}