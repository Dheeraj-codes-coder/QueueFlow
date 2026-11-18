
package com.queueflow.dto;

import java.time.LocalDate;

public class PatientRegistrationResponse {

    private Long patientId;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;

    public PatientRegistrationResponse(
            Long patientId,
            Long userId,
            String fullName,
            String email,
            String phone,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup) {

        this.patientId = patientId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
    }

    public Long getPatientId() {
        return patientId;
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

    public String getPhone() {
        return phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }
}
