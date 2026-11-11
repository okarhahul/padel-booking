package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSlotRepository extends JpaRepository<BookingSlot, Long> {
}