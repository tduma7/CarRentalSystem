package com.carrental.service;

import com.carrental.dto.*;
import com.carrental.exception.NoAvailableCarsException;
import com.carrental.model.*;
import com.carrental.repository.CarRepository;
import com.carrental.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarRentalServiceImpl implements CarRentalService {
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    public CarRentalServiceImpl(CarRepository carRepository, ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional
    public ReservationResponseDto makeReservation(CreateReservationRequest request, String userId) {
        if (!request.startDateTime().isBefore(request.endDateTime()))
            throw new IllegalArgumentException("Start date/time must be before end date/time");
        Car car = carRepository.findByIdWithLock(request.carId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + request.carId()));
        if (reservationRepository.isCarReservedInPeriod(car.getId(), request.startDateTime(), request.endDateTime()))
            throw new NoAvailableCarsException("Car %s is unavailable for period (%s - %s)".formatted(car.getId(), request.startDateTime(), request.endDateTime()));
        return mapToResponseDto(reservationRepository.save(new ReservationEntity(car, request.startDateTime(), request.endDateTime(), userId)));
    }

    @Override
    @Transactional
    public boolean cancelReservation(String reservationId, String userId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null || !reservation.getUserId().equals(userId) || reservation.getStatus() == ReservationStatus.CANCELLED)
            return false;
        reservation.setStatus(ReservationStatus.CANCELLED);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDto> getAvailableCars(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!startDateTime.isBefore(endDateTime))
            throw new IllegalArgumentException("Start date/time must be before end date/time");
        return carRepository.findAvailableCars(startDateTime, endDateTime).stream().map(this::mapToCarDto).toList();
    }

    private CarDto mapToCarDto(Car car) {
        return new CarDto(car.getId(), car.getType(), car.getLicensePlate(), car.getDailyRate());
    }

    private ReservationResponseDto mapToResponseDto(ReservationEntity e) {
        return new ReservationResponseDto(e.getReservationId(), mapToCarDto(e.getCar()), e.getStartDateTime(), e.getEndDateTime(), e.getUserId(), e.getTotalPrice(), e.getStatus());
    }
}
