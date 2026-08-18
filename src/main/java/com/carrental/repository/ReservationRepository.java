package com.carrental.repository;

import com.carrental.model.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<ReservationEntity, String> {
    @Query("""
            SELECT COUNT(r) > 0 FROM ReservationEntity r WHERE r.car.id = :carId
            AND r.status = com.carrental.model.ReservationStatus.ACTIVE
            AND r.startDateTime < :endDateTime AND r.endDateTime > :startDateTime
            """)
    boolean isCarReservedInPeriod(@Param("carId") String carId,
                                  @Param("startDateTime") LocalDateTime startDateTime,
                                  @Param("endDateTime") LocalDateTime endDateTime);
}
