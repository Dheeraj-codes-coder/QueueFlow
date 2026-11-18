
package com.queueflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.queueflow.enums.QueueStatus;

public class QueueTicketResponse {

    private Long queueTicketId;
    private Integer tokenNumber;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private LocalDate queueDate;
    private LocalDateTime checkInTime;
    private QueueStatus status;

    public QueueTicketResponse(
            Long queueTicketId,
            Integer tokenNumber,
            Long patientId,
            String patientName,
            Long doctorId,
            LocalDate queueDate,
            LocalDateTime checkInTime,
            QueueStatus status) {

        this.queueTicketId = queueTicketId;
        this.tokenNumber = tokenNumber;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.queueDate = queueDate;
        this.checkInTime = checkInTime;
        this.status = status;
    }

    public Long getQueueTicketId() {
        return queueTicketId;
    }

    public Integer getTokenNumber() {
        return tokenNumber;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public LocalDate getQueueDate() {
        return queueDate;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public QueueStatus getStatus() {
        return status;
    }
}
