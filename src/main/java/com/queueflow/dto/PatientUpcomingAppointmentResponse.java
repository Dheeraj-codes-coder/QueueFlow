package com.queueflow.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.queueflow.enums.AppointmentStatus;

public class PatientUpcomingAppointmentResponse {

    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private String clinicName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
    private String reason;

    public PatientUpcomingAppointmentResponse(
            Long appointmentId,
            Long doctorId,
            String doctorName,
            String clinicName,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            AppointmentStatus status,
            String reason) {

        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.clinicName = clinicName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.reason = reason;
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
}