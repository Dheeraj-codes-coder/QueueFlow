
package com.queueflow.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.queueflow.entity.AppointmentSlot;

import jakarta.persistence.LockModeType;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    boolean existsByDoctor_IdAndSlotDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            LocalDate slotDate,
            LocalTime newEndTime,
            LocalTime newStartTime
    );

    boolean existsByDoctor_IdAndSlotDateAndStartTime(
            Long doctorId,
            LocalDate slotDate,
            LocalTime startTime
    );

    List<AppointmentSlot> findAllByDoctor_IdOrderBySlotDateAscStartTimeAsc(Long doctorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AppointmentSlot s WHERE s.id = :slotId")
    Optional<AppointmentSlot> findByIdForUpdate(@Param("slotId") Long slotId);
}
