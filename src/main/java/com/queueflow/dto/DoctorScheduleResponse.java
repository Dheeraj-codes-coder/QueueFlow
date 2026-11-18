
package com.queueflow.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorScheduleResponse {

    private Long scheduleId;
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public DoctorScheduleResponse(
            Long scheduleId,
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            boolean available) {

        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isAvailable() {
        return available;
    }
}
