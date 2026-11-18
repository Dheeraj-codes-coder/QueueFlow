package com.queueflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PatientPrescriptionHistoryResponse {

    private Long prescriptionId;
    private Long consultationId;
    private LocalDateTime consultationStartedAt;
    private LocalDateTime consultationEndedAt;
    private String consultationNotes;
    private LocalDateTime prescribedAt;
    private String instructions;
    private List<PrescriptionItemResponse> medicines;

    public PatientPrescriptionHistoryResponse(
            Long prescriptionId,
            Long consultationId,
            LocalDateTime consultationStartedAt,
            LocalDateTime consultationEndedAt,
            String consultationNotes,
            LocalDateTime prescribedAt,
            String instructions,
            List<PrescriptionItemResponse> medicines) {

        this.prescriptionId = prescriptionId;
        this.consultationId = consultationId;
        this.consultationStartedAt = consultationStartedAt;
        this.consultationEndedAt = consultationEndedAt;
        this.consultationNotes = consultationNotes;
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

    public LocalDateTime getConsultationStartedAt() {
        return consultationStartedAt;
    }

    public LocalDateTime getConsultationEndedAt() {
        return consultationEndedAt;
    }

    public String getConsultationNotes() {
        return consultationNotes;
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