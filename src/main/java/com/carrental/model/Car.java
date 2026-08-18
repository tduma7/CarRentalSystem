package com.carrental.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cars")
public class Car {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType type;
    @Column(nullable = false, unique = true)
    private String licensePlate;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    protected Car() {
    }

    public Car(String id, CarType type, String licensePlate, BigDecimal dailyRate) {
        this.id = Objects.requireNonNull(id, "Car ID cannot be null");
        this.type = Objects.requireNonNull(type, "Car type cannot be null");
        this.licensePlate = Objects.requireNonNull(licensePlate, "License plate cannot be null");
        if (dailyRate == null || dailyRate.signum() <= 0)
            throw new IllegalArgumentException("Daily rate must be positive");
        this.dailyRate = dailyRate;
    }

    public String getId() {
        return id;
    }

    public CarType getType() {
        return type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Car car && Objects.equals(id, car.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
