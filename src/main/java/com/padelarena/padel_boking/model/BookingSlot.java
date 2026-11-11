package com.padelarena.padel_boking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "booking_slots")
public class BookingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "slot_hour", nullable = false)
    private Integer slotHour;

    // Wajib ada untuk JPA
    protected BookingSlot() {
    }

    public BookingSlot(Booking booking, Court court, LocalDate playDate, int slotHour) {
        this.booking = booking;
        this.court = court;
        this.playDate = playDate;
        this.slotHour = slotHour;
    }

    public Long getId() { return id; }
    public Booking getBooking() { return booking; }
    public Court getCourt() { return court; }
    public LocalDate getPlayDate() { return playDate; }
    public Integer getSlotHour() { return slotHour; }
}