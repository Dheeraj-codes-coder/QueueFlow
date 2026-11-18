
package com.queueflow.dto;

public class AppointmentSlotGenerationResponse {

    private Long doctorId;
    private int totalSlotsGenerated;
    private String message;

    public AppointmentSlotGenerationResponse(
            Long doctorId,
            int totalSlotsGenerated,
            String message) {

        this.doctorId = doctorId;
        this.totalSlotsGenerated = totalSlotsGenerated;
        this.message = message;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public int getTotalSlotsGenerated() {
        return totalSlotsGenerated;
    }

    public String getMessage() {
        return message;
    }
}
