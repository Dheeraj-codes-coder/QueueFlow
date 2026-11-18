
package com.queueflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionDetailsResponse {

    private Long prescriptionId;
    private Long consultationId;
    private LocalDateTime prescribedAt;
    private String instructions;
    private List<PrescriptionItemResponse> medicines;

    public PrescriptionDetailsResponse(
            Long prescriptionId,
            Long consultationId,
            LocalDateTime prescribedAt,
            String instructions,
            List<PrescriptionItemResponse> medicines) {

        this.prescriptionId = prescriptionId;
        this.consultationId = consultationId;
        this.prescribedAt = prescribedAt;
        this.instructions = instructions;
        this.medicines = medicines;
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

    public List<PrescriptionItemResponse> getMedicines() {
        return medicines;
    }
}
