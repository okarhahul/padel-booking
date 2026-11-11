package com.padelarena.padel_boking.repository;

import com.padelarena.padel_boking.model.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}