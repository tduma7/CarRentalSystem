package com.carrental.dto;

import com.carrental.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponseDto(String reservationId, CarDto car, LocalDateTime startDateTime,
                                     LocalDateTime endDateTime, String userId, BigDecimal totalPrice,
                                     ReservationStatus status) {
}
