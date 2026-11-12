package com.padelarena.padel_boking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "start_hour", nullable = false)
    private Integer startHour;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours = 1;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 10)
    private BookingStatus status = BookingStatus.HOLD;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "payment_method", length = 10)
    private PaymentMethod paymentMethod;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Wajib ada untuk JPA
    protected Booking() {
    }

    public Booking(User user, Court court, LocalDate playDate,
                   int startHour, int durationHours, int totalPrice) {
        this.user = user;
        this.court = court;
        this.playDate = playDate;
        this.startHour = startHour;
        this.durationHours = durationHours;
        this.totalPrice = totalPrice;
    }


    public Long getId() { return id; }
    public User getUser() { return user; }
    public Court getCourt() { return court; }
    public LocalDate getPlayDate() { return playDate; }
    public Integer getStartHour() { return startHour; }
    public Integer getDurationHours() { return durationHours; }
    public BookingStatus getStatus() { return status; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Integer getTotalPrice() { return totalPrice; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void holdUntil(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}