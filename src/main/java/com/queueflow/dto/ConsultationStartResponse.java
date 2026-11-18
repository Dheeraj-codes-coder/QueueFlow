
package com.queueflow.dto;

import java.time.LocalDateTime;

import com.queueflow.enums.QueueStatus;

public class ConsultationStartResponse {

    private Long consultationId;
    private Long queueTicketId;
    private Integer tokenNumber;
    private String patientName;
    private QueueStatus queueStatus;
    private LocalDateTime startedAt;
    private String message;

    public ConsultationStartResponse(
            Long consultationId,
            Long queueTicketId,
            Integer tokenNumber,
            String patientName,
            QueueStatus queueStatus,
            LocalDateTime startedAt,
            String message) {

        this.consultationId = consultationId;
        this.queueTicketId = queueTicketId;
        this.tokenNumber = tokenNumber;
        this.patientName = patientName;
        this.queueStatus = queueStatus;
        this.startedAt = startedAt;
        this.message = message;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public Long getQueueTicketId() {
        return queueTicketId;
    }

    public Integer getTokenNumber() {
        return tokenNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public QueueStatus getQueueStatus() {
        return queueStatus;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public String getMessage() {
        return message;
    }
}
