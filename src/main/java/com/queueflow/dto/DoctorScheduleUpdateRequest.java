
package com.queueflow.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public class DoctorScheduleUpdateRequest {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private boolean available;

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
