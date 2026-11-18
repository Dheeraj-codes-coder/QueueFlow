
package com.queueflow.dto;

import com.queueflow.enums.AppointmentStatus;

public class AppointmentBookingResponse {

    private Long appointmentId;

    private Long slotId;

    private AppointmentStatus status;

    private String message;

    public AppointmentBookingResponse() {

    }

    public AppointmentBookingResponse(
            Long appointmentId,
            Long slotId,
            AppointmentStatus status,
            String message) {

        this.appointmentId = appointmentId;
        this.slotId = slotId;
        this.status = status;
        this.message = message;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
