
package com.queueflow.dto;

import java.time.LocalDate;

import com.queueflow.enums.QueueStatus;

public class QueueCheckInResponse {

    private Long queueTicketId;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private Integer tokenNumber;
    private LocalDate queueDate;
    private QueueStatus status;
    private String message;

    public QueueCheckInResponse(
            Long queueTicketId,
            Long appointmentId,
            Long patientId,
            Long doctorId,
            Integer tokenNumber,
            LocalDate queueDate,
            QueueStatus status,
            String message) {

        this.queueTicketId = queueTicketId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.tokenNumber = tokenNumber;
        this.queueDate = queueDate;
        this.status = status;
        this.message = message;
    }

    public Long getQueueTicketId() {
        return queueTicketId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Integer getTokenNumber() {
        return tokenNumber;
    }

    public LocalDate getQueueDate() {
        return queueDate;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
