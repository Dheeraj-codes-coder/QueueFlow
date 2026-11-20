
package com.queueflow.dto;

import com.queueflow.enums.DoctorSpecialty;

import jakarta.validation.constraints.NotNull;

public class DoctorSpecialtyUpdateRequest {

    @NotNull(message = "Specialty is required")
    private DoctorSpecialty specialty;

    public DoctorSpecialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(DoctorSpecialty specialty) {
        this.specialty = specialty;
    }
}
