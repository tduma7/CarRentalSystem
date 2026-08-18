package com.carrental.service;

import com.carrental.dto.CarDto;
import com.carrental.dto.CreateReservationRequest;
import com.carrental.dto.ReservationResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CarRentalService {
    ReservationResponseDto makeReservation(CreateReservationRequest request, String userId);

    boolean cancelReservation(String reservationId, String userId);

    List<CarDto> getAvailableCars(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
