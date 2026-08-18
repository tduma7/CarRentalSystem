package com.carrental.service;

import com.carrental.dto.*;
import com.carrental.exception.NoAvailableCarsException;
import com.carrental.model.*;
import com.carrental.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarRentalServiceImplTest {
    @Mock
    CarRepository carRepository;
    @Mock
    ReservationRepository reservationRepository;
    @InjectMocks
    CarRentalServiceImpl carRentalService;
    private Car car;
    private final LocalDateTime now = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setUp() {
        car = new Car("car-1", CarType.SEDAN, "KR-12345", new BigDecimal("100.00"));
    }

    @Test
    void shouldCreateReservation() {
        CreateReservationRequest request = new CreateReservationRequest("car-1", now, now.plusDays(2));
        when(carRepository.findByIdWithLock("car-1")).thenReturn(Optional.of(car));
        when(reservationRepository.isCarReservedInPeriod("car-1", now, now.plusDays(2))).thenReturn(false);
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(i -> i.getArgument(0));
        ReservationResponseDto response = carRentalService.makeReservation(request, "user-123");
        assertThat(response.userId()).isEqualTo("user-123");
        assertThat(response.totalPrice()).isEqualByComparingTo("200.00");
        verify(reservationRepository).save(any(ReservationEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenCarIsReserved() {
        CreateReservationRequest request = new CreateReservationRequest("car-1", now, now.plusDays(1));
        when(carRepository.findByIdWithLock("car-1")).thenReturn(Optional.of(car));
        when(reservationRepository.isCarReservedInPeriod("car-1", now, now.plusDays(1))).thenReturn(true);
        assertThatThrownBy(() -> carRentalService.makeReservation(request, "user-123")).isInstanceOf(NoAvailableCarsException.class);
        verify(reservationRepository, never()).save(any());
    }
}
