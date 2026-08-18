package com.carrental.controller;

import com.carrental.dto.*;
import com.carrental.service.CarRentalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final CarRentalService carRentalService;

    public ReservationController(CarRentalService carRentalService) {
        this.carRentalService = carRentalService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@Valid @RequestBody CreateReservationRequest request, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carRentalService.makeReservation(request, user.getUsername()));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String reservationId, @AuthenticationPrincipal UserDetails user) {
        return carRentalService.cancelReservation(reservationId, user.getUsername()) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/availability")
    public List<CarDto> getAvailableCars(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime) {
        return carRentalService.getAvailableCars(startDateTime, endDateTime);
    }
}
