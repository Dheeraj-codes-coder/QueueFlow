package com.queueflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.queueflow.enums.AppointmentStatus;

public class DoctorConsultationHistoryResponse {

    private Long consultationId;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private LocalDate appointmentDate;
    private LocalTime appointmentStartTime;
    private LocalTime appointmentEndTime;
    private AppointmentStatus appointmentStatus;
    private String appointmentReason;
    private LocalDateTime consultationStartedAt;
    private LocalDateTime consultationEndedAt;
    private String consultationNotes;

    public DoctorConsultationHistoryResponse(
            Long consultationId,
            Long appointmentId,
            Long patientId,
            String patientName,
            String patientEmail,
            String patientPhone,
            LocalDate appointmentDate,
            LocalTime appointmentStartTime,
            LocalTime appointmentEndTime,
            AppointmentStatus appointmentStatus,
            String appointmentReason,
            LocalDateTime consultationStartedAt,
            LocalDateTime consultationEndedAt,
            String consultationNotes) {

        this.consultationId = consultationId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.appointmentDate = appointmentDate;
        this.appointmentStartTime = appointmentStartTime;
        this.appointmentEndTime = appointmentEndTime;
        this.appointmentStatus = appointmentStatus;
        this.appointmentReason = appointmentReason;
        this.consultationStartedAt = consultationStartedAt;
        this.consultationEndedAt = consultationEndedAt;
        this.consultationNotes = consultationNotes;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentStartTime() {
        return appointmentStartTime;
    }

    public LocalTime getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public String getAppointmentReason() {
        return appointmentReason;
    }

    public LocalDateTime getConsultationStartedAt() {
        return consultationStartedAt;
    }

    public LocalDateTime getConsultationEndedAt() {
        return consultationEndedAt;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }
}