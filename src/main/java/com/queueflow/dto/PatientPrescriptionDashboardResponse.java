
package com.queueflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PatientPrescriptionDashboardResponse {

    private Long prescriptionId;
    private Long consultationId;
    private String doctorName;
    private LocalDateTime prescribedAt;
    private String instructions;
    private List<MedicineResponse> medicines;

    public PatientPrescriptionDashboardResponse(
            Long prescriptionId,
            Long consultationId,
            String doctorName,
            LocalDateTime prescribedAt,
            String instructions,
            List<MedicineResponse> medicines) {

        this.prescriptionId = prescriptionId;
        this.consultationId = consultationId;
        this.doctorName = doctorName;
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

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<MedicineResponse> getMedicines() {
        return medicines;
    }

    public static class MedicineResponse {

        private Long prescriptionItemId;
        private String medicineName;
        private String dosage;
        private String frequency;
        private int durationDays;
        private int quantity;
        private String instructions;

        public MedicineResponse(
                Long prescriptionItemId,
                String medicineName,
                String dosage,
                String frequency,
                int durationDays,
                int quantity,
                String instructions) {

            this.prescriptionItemId = prescriptionItemId;
            this.medicineName = medicineName;
            this.dosage = dosage;
            this.frequency = frequency;
            this.durationDays = durationDays;
            this.quantity = quantity;
            this.instructions = instructions;
        }

        public Long getPrescriptionItemId() {
            return prescriptionItemId;
        }

        public String getMedicineName() {
            return medicineName;
        }

        public String getDosage() {
            return dosage;
        }

        public String getFrequency() {
            return frequency;
        }

        public int getDurationDays() {
            return durationDays;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getInstructions() {
            return instructions;
        }
    }
}
