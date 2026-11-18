package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Ubah pesanan yang waktunya habis menjadi EXPIRED (riwayat tetap tersimpan)
    @Modifying(flushAutomatically = true)
    @Query("update Booking b set b.status = com.padelarena.padel_boking.model.BookingStatus.EXPIRED "
            + "where b.status in :statuses and b.expiresAt < :now")
    int markExpired(@Param("statuses") Collection<BookingStatus> statuses,
                    @Param("now") LocalDateTime now);

    // Cari pesanan HANYA kalau milik user ini
    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    // Daftar pesanan satu user. EntityGraph: data lapangan ikut diambil dalam satu query,
    // supaya tidak ada satu query tambahan untuk setiap baris.
    @EntityGraph(attributePaths = "court")
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "court")
    Page<Booking> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);
}