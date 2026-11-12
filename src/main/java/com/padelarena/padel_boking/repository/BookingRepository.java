package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Hapus pesanan berstatus tertentu yang waktunya sudah lewat.
    // Slot-nya ikut terhapus otomatis (ON DELETE CASCADE di database).
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Booking b where b.status = :status and b.expiresAt < :now")
    int deleteExpired(@Param("status") BookingStatus status, @Param("now") LocalDateTime now);
}