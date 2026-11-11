package com.padelarena.padel_boking;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.padelarena.padel_boking.model.Booking;
import com.padelarena.padel_boking.model.BookingSlot;
import com.padelarena.padel_boking.model.Court;
import com.padelarena.padel_boking.model.User;
import com.padelarena.padel_boking.repository.BookingRepository;
import com.padelarena.padel_boking.repository.BookingSlotRepository;
import com.padelarena.padel_boking.repository.CourtRepository;
import com.padelarena.padel_boking.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional   // semua data tes dibatalkan otomatis di akhir
class DoubleBookingTest {

    private static final LocalDate TANGGAL = LocalDate.of(2030, 1, 1);
    private static final int JAM = 19;

    @Autowired CourtRepository courtRepository;
    @Autowired UserRepository userRepository;
    @Autowired BookingRepository bookingRepository;
    @Autowired BookingSlotRepository slotRepository;

    private User buatUser() {
        String email = "tes-" + UUID.randomUUID() + "@padel.test";
        return userRepository.save(new User("Penguji", email, "081200000000", "hash-palsu"));
    }

    private Booking buatBooking(User user, Court court) {
        return bookingRepository.save(new Booking(user, court, TANGGAL, JAM, 1, 200000));
    }

    @Test
    void jamSamaDiLapanganSama_pemesanKeduaDitolak() {
        Court court = courtRepository.findAll().get(0);

        Booking b1 = buatBooking(buatUser(), court);
        slotRepository.saveAndFlush(new BookingSlot(b1, court, TANGGAL, JAM));

        Booking b2 = buatBooking(buatUser(), court);

        assertThrows(DataIntegrityViolationException.class, () ->
                slotRepository.saveAndFlush(new BookingSlot(b2, court, TANGGAL, JAM)));
    }

    @Test
    void jamSamaDiLapanganBeda_keduanyaBoleh() {
        List<Court> courts = courtRepository.findAll();
        Court lapanganA = courts.get(0);
        Court lapanganB = courts.get(1);

        Booking b1 = buatBooking(buatUser(), lapanganA);
        Booking b2 = buatBooking(buatUser(), lapanganB);

        assertDoesNotThrow(() -> {
            slotRepository.saveAndFlush(new BookingSlot(b1, lapanganA, TANGGAL, JAM));
            slotRepository.saveAndFlush(new BookingSlot(b2, lapanganB, TANGGAL, JAM));
        });
    }
}