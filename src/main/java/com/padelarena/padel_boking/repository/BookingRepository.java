package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}