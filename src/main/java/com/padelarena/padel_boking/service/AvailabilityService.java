package com.padelarena.padel_boking.service;

import com.padelarena.padel_boking.dto.SlotResponse;
import com.padelarena.padel_boking.model.BookingSlot;
import com.padelarena.padel_boking.repository.BookingSlotRepository;
import com.padelarena.padel_boking.repository.CourtRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AvailabilityService {

    public static final ZoneId ZONE = ZoneId.of("Asia/Jakarta");
    public static final int FIRST_HOUR = 7;    // slot pertama mulai 07:00
    public static final int LAST_HOUR = 21;    // slot terakhir mulai 21:00 (selesai 22:00)
    public static final int MAX_DAYS = 90;     // jendela booking

    private final CourtRepository courtRepository;
    private final BookingSlotRepository slotRepository;

    public AvailabilityService(CourtRepository courtRepository, BookingSlotRepository slotRepository) {
        this.courtRepository = courtRepository;
        this.slotRepository = slotRepository;
    }

    public static int priceFor(int hour) {
        return hour >= 17 ? 200_000 : 150_000;   // jam sibuk lebih mahal
    }

    public List<SlotResponse> getAvailability(Long courtId, LocalDate date) {
        if (!courtRepository.existsById(courtId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lapangan tidak ditemukan");
        }

        LocalDate today = LocalDate.now(ZONE);
        if (date.isBefore(today) || date.isAfter(today.plusDays(MAX_DAYS))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tanggal di luar rentang booking (hari ini sampai " + MAX_DAYS + " hari ke depan)");
        }

        Set<Integer> booked = slotRepository.findByCourtIdAndPlayDate(courtId, date).stream()
                .map(BookingSlot::getSlotHour)
                .collect(Collectors.toSet());

        int nowHour = LocalTime.now(ZONE).getHour();
        List<SlotResponse> result = new ArrayList<>();
        for (int h = FIRST_HOUR; h <= LAST_HOUR; h++) {
            String status;
            if (date.equals(today) && h <= nowHour) {
                status = "PAST";          // jam yang sudah mulai/lewat hari ini
            } else if (booked.contains(h)) {
                status = "BOOKED";
            } else {
                status = "AVAILABLE";
            }
            result.add(new SlotResponse(h, String.format("%02d:00", h), priceFor(h), status));
        }
        return result;
    }
}