
package com.queueflow.dto;

public class PrescriptionItemResponse {

    private Long prescriptionItemId;
    private Long prescriptionId;
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private Integer quantity;
    private String instructions;
    private String message;

    public PrescriptionItemResponse(
            Long prescriptionItemId,
            Long prescriptionId,
            String medicineName,
            String dosage,
            String frequency,
            Integer durationDays,
            Integer quantity,
            String instructions,
            String message) {

        this.prescriptionItemId = prescriptionItemId;
        this.prescriptionId = prescriptionId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.quantity = quantity;
        this.instructions = instructions;
        this.message = message;
    }

    public Long getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
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

    public Integer getDurationDays() {
        return durationDays;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getMessage() {
        return message;
    }
}
