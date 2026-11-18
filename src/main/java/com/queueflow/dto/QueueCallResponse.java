
package com.queueflow.dto;

import com.queueflow.enums.QueueStatus;

public class QueueCallResponse {

    private Long queueTicketId;
    private Integer tokenNumber;
    private String patientName;
    private QueueStatus status;
    private String message;

    public QueueCallResponse(
            Long queueTicketId,
            Integer tokenNumber,
            String patientName,
            QueueStatus status,
            String message) {

        this.queueTicketId = queueTicketId;
        this.tokenNumber = tokenNumber;
        this.patientName = patientName;
        this.status = status;
        this.message = message;
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

    public QueueStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
