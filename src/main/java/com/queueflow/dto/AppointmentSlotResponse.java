
package com.queueflow.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.queueflow.enums.SlotStatus;

public class AppointmentSlotResponse {

    private Long slotId;
    private Long doctorId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private SlotStatus status;

    public AppointmentSlotResponse(
            Long slotId,
            Long doctorId,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime,
            SlotStatus status) {

        this.slotId = slotId;
        this.doctorId = doctorId;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getSlotId() {
        return slotId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public SlotStatus getStatus() {
        return status;
    }
}
