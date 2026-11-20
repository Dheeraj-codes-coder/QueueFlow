
package com.queueflow.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.DoctorRegistrationRequest;
import com.queueflow.dto.DoctorRegistrationResponse;
import com.queueflow.dto.DoctorSearchResponse;
import com.queueflow.dto.DoctorSpecialtyUpdateRequest;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.enums.DoctorSpecialty;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorController(
            DoctorService doctorService,
            DoctorProfileRepository doctorProfileRepository) {

        this.doctorService = doctorService;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<DoctorRegistrationResponse> registerDoctor(
            @Valid @RequestBody DoctorRegistrationRequest request) {

        DoctorProfile doctor = doctorService.registerDoctor(request);

        DoctorRegistrationResponse response = new DoctorRegistrationResponse(
                doctor.getId(),
                doctor.getClinicStaff().getUser().getId(),
                doctor.getClinicStaff().getUser().getFullName(),
                doctor.getClinicStaff().getUser().getEmail(),
                doctor.getClinicStaff().getClinic().getId(),
                doctor.getClinicStaff().getClinic().getName(),
                doctor.getQualification(),
                doctor.getSpecialty(),
                doctor.getExperienceYears(),
                doctor.getConsultationDurationMinutes(),
                doctor.getConsultationFee(),
                doctor.isActive()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/specialties")
    public ResponseEntity<List<DoctorSpecialty>> getSpecialties() {

        List<DoctorSpecialty> specialties =
                List.of(DoctorSpecialty.values());

        return ResponseEntity.ok(specialties);
    }

    @GetMapping
    public ResponseEntity<List<DoctorSearchResponse>> getDoctors(
            @RequestParam(required = false) DoctorSpecialty specialty) {

        List<DoctorProfile> doctors;

        if (specialty == null) {
            doctors = doctorProfileRepository
                    .findByActiveTrueOrderBySpecialtyAscExperienceYearsDesc();
        } else {
            doctors = doctorProfileRepository
                    .findByActiveTrueAndSpecialtyOrderByExperienceYearsDesc(
                            specialty
                    );
        }

        List<DoctorSearchResponse> response = new ArrayList<>();

        for (DoctorProfile doctor : doctors) {

            DoctorSearchResponse doctorResponse =
                    new DoctorSearchResponse(
                            doctor.getId(),
                            doctor.getClinicStaff().getUser().getFullName(),
                            doctor.getClinicStaff().getClinic().getName(),
                            doctor.getSpecialty(),
                            doctor.getQualification(),
                            doctor.getExperienceYears(),
                            doctor.getConsultationFee()
                    );

            response.add(doctorResponse);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{doctorId}/specialty")
    public ResponseEntity<?> updateDoctorSpecialty(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorSpecialtyUpdateRequest request) {

        DoctorProfile doctor =
                doctorProfileRepository.findById(doctorId).orElse(null);

        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Doctor not found");
        }

        doctor.setSpecialty(request.getSpecialty());

        doctorProfileRepository.save(doctor);

        return ResponseEntity.ok(
                "Doctor specialty updated successfully"
        );
    }
}
