
package com.queueflow.dto;

import jakarta.validation.constraints.NotNull;

public class QueueCheckInRequest {

    @NotNull
    private Long appointmentId;

    public QueueCheckInRequest() {
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}
