
package com.queueflow.dto;

import java.time.LocalDateTime;

import com.queueflow.enums.AppointmentStatus;
import com.queueflow.enums.QueueStatus;

public class ConsultationCompleteResponse {

    private Long consultationId;
    private Long queueTicketId;
    private int tokenNumber;
    private String patientName;
    private QueueStatus queueStatus;
    private AppointmentStatus appointmentStatus;
    private LocalDateTime endedAt;
    private String message;

    public ConsultationCompleteResponse(
            Long consultationId,
            Long queueTicketId,
            int tokenNumber,
            String patientName,
            QueueStatus queueStatus,
            AppointmentStatus appointmentStatus,
            LocalDateTime endedAt,
            String message) {

        this.consultationId = consultationId;
        this.queueTicketId = queueTicketId;
        this.tokenNumber = tokenNumber;
        this.patientName = patientName;
        this.queueStatus = queueStatus;
        this.appointmentStatus = appointmentStatus;
        this.endedAt = endedAt;
        this.message = message;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public Long getQueueTicketId() {
        return queueTicketId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public QueueStatus getQueueStatus() {
        return queueStatus;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public String getMessage() {
        return message;
    }
}
