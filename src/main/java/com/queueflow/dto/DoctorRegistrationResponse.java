
package com.queueflow.dto;

import java.math.BigDecimal;

public class DoctorRegistrationResponse {

    private Long doctorId;
    private Long userId;
    private String fullName;
    private String email;
    private Long clinicId;
    private String clinicName;
    private String qualification;
    private int experienceYears;
    private int consultationDurationMinutes;
    private BigDecimal consultationFee;
    private boolean active;

    public DoctorRegistrationResponse(
            Long doctorId,
            Long userId,
            String fullName,
            String email,
            Long clinicId,
            String clinicName,
            String qualification,
            int experienceYears,
            int consultationDurationMinutes,
            BigDecimal consultationFee,
            boolean active) {

        this.doctorId = doctorId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationDurationMinutes = consultationDurationMinutes;
        this.consultationFee = consultationFee;
        this.active = active;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getQualification() {
        return qualification;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public int getConsultationDurationMinutes() {
        return consultationDurationMinutes;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public boolean isActive() {
        return active;
    }
}
