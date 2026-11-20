
package com.queueflow.dto;

import java.math.BigDecimal;

import com.queueflow.enums.DoctorSpecialty;

public class DoctorSearchResponse {

    private Long doctorId;
    private String fullName;
    private String clinicName;
    private DoctorSpecialty specialty;
    private String qualification;
    private int experienceYears;
    private BigDecimal consultationFee;

    public DoctorSearchResponse(
            Long doctorId,
            String fullName,
            String clinicName,
            DoctorSpecialty specialty,
            String qualification,
            int experienceYears,
            BigDecimal consultationFee) {

        this.doctorId = doctorId;
        this.fullName = fullName;
        this.clinicName = clinicName;
        this.specialty = specialty;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public DoctorSpecialty getSpecialty() {
        return specialty;
    }

    public String getQualification() {
        return qualification;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }
}
