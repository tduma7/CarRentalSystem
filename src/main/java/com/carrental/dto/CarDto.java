package com.carrental.dto;

import com.carrental.model.CarType;

import java.math.BigDecimal;

public record CarDto(String carId, CarType type, String licensePlate, BigDecimal dailyRate) {
}
