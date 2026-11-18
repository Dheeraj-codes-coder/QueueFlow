
package com.queueflow.dto;

import java.time.LocalDateTime;

public class PrescriptionResponse {

    private Long prescriptionId;
    private Long consultationId;
    private LocalDateTime prescribedAt;
    private String instructions;
    private String message;

    public PrescriptionResponse(
            Long prescriptionId,
            Long consultationId,
            LocalDateTime prescribedAt,
            String instructions,
            String message) {

        this.prescriptionId = prescriptionId;
        this.consultationId = consultationId;
        this.prescribedAt = prescribedAt;
        this.instructions = instructions;
        this.message = message;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getMessage() {
        return message;
    }
}
