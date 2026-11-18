package com.queueflow.dto;

import jakarta.validation.constraints.NotNull;

public class AppointmentRescheduleRequest {

    @NotNull
    private Long patientId;

    @NotNull
    private Long newSlotId;

    public AppointmentRescheduleRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getNewSlotId() {
        return newSlotId;
    }

    public void setNewSlotId(Long newSlotId) {
        this.newSlotId = newSlotId;
    }
}