
package com.queueflow.dto;

import jakarta.validation.constraints.NotNull;

public class PrescriptionCreateRequest {

    @NotNull
    private Long consultationId;

    private String instructions;

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
