package com.carrental.repository;

import com.carrental.model.Car;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Car c WHERE c.id = :id")
    Optional<Car> findByIdWithLock(@Param("id") String id);

    @Query("""
            SELECT c FROM Car c WHERE NOT EXISTS (
                SELECT r FROM ReservationEntity r WHERE r.car.id = c.id
                AND r.status = com.carrental.model.ReservationStatus.ACTIVE
                AND r.startDateTime < :endDateTime AND r.endDateTime > :startDateTime
            )
            """)
    List<Car> findAvailableCars(@Param("startDateTime") LocalDateTime startDateTime,
                                @Param("endDateTime") LocalDateTime endDateTime);
}
