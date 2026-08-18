package com.carrental.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "reservations", indexes = @Index(name = "idx_reservation_car_dates", columnList = "car_id, start_date_time, end_date_time, status"))
public class ReservationEntity {
    @Id
    private String reservationId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    @Column(nullable = false)
    private LocalDateTime endDateTime;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    protected ReservationEntity() {
    }

    public ReservationEntity(Car car, LocalDateTime startDateTime, LocalDateTime endDateTime, String userId) {
        this.reservationId = UUID.randomUUID().toString();
        this.car = Objects.requireNonNull(car, "Car cannot be null");
        this.startDateTime = Objects.requireNonNull(startDateTime, "Start date/time cannot be null");
        this.endDateTime = Objects.requireNonNull(endDateTime, "End date/time cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        if (!startDateTime.isBefore(endDateTime))
            throw new IllegalArgumentException("Start date/time must be before end date/time");
        this.totalPrice = calculatePrice(car.getDailyRate(), startDateTime, endDateTime);
        this.status = ReservationStatus.ACTIVE;
    }

    private BigDecimal calculatePrice(BigDecimal rate, LocalDateTime start, LocalDateTime end) {
        long hours = ChronoUnit.HOURS.between(start, end);
        long days = Math.max(1, (long) Math.ceil(hours / 24.0));
        return rate.multiply(BigDecimal.valueOf(days));
    }

    public String getReservationId() {
        return reservationId;
    }

    public Car getCar() {
        return car;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ReservationEntity r && Objects.equals(reservationId, r.reservationId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
}
