
package com.queueflow.dto;

import com.queueflow.enums.AppointmentStatus;

public class AppointmentCancellationResponse {

    private Long appointmentId;
    private AppointmentStatus status;
    private String message;

    public AppointmentCancellationResponse(
            Long appointmentId,
            AppointmentStatus status,
            String message) {

        this.appointmentId = appointmentId;
        this.status = status;
        this.message = message;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
