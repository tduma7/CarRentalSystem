package com.carrental.controller;

import com.carrental.dto.CreateReservationRequest;
import com.carrental.model.*;
import com.carrental.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CarRepository carRepository;
    @Autowired
    ReservationRepository reservationRepository;
    private final LocalDateTime start = LocalDateTime.now().plusDays(2);
    private final LocalDateTime end = start.plusDays(3);

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        carRepository.deleteAll();
        carRepository.save(new Car("car-test-1", CarType.SUV, "KR-99999", new BigDecimal("150.00")));
    }

    @Test
    void shouldReturnAvailableCars() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/availability").param("startDateTime", start.toString()).param("endDateTime", end.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].carId").value("car-test-1"));
    }

    @Test
    @WithMockUser(username = "user-123")
    void shouldCreateReservationAndPreventConflict() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest("car-test-1", start, end);
        mockMvc.perform(post("/api/v1/reservations").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.userId").value("user-123")).andExpect(jsonPath("$.totalPrice").value(450.00));
        mockMvc.perform(post("/api/v1/reservations").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
    }
}
