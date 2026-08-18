package com.carrental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateReservationRequest(@NotBlank(message = "Car ID is required") String carId,
                                       @NotNull(message = "Start date/time is required") @FutureOrPresent(message = "Start date must be in present or future") LocalDateTime startDateTime,
                                       @NotNull(message = "End date/time is required") LocalDateTime endDateTime) {
}
