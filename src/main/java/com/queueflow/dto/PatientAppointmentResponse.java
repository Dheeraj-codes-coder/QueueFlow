
package com.queueflow.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import com.queueflow.enums.AppointmentStatus;

public class PatientAppointmentResponse {

    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private String clinicName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String reason;
    private LocalDateTime bookedAt;

    public PatientAppointmentResponse(
            Long appointmentId,
            Long doctorId,
            String doctorName,
            String clinicName,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            AppointmentStatus status,
            String reason,
            LocalDateTime bookedAt) {

        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.clinicName = clinicName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.reason = reason;
        this.bookedAt = bookedAt;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }
}
