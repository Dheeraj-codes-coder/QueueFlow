
package com.queueflow.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    boolean existsByDoctor_IdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime newEndTime,
            LocalTime newStartTime
    );

    boolean existsByDoctor_IdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime newEndTime,
            LocalTime newStartTime,
            Long scheduleId
    );

    List<DoctorSchedule> findAllByDoctor_IdOrderByDayOfWeekAscStartTimeAsc(Long doctorId);
}
