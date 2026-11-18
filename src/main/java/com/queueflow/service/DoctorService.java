
package com.queueflow.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.DoctorRegistrationRequest;
import com.queueflow.entity.AppUser;
import com.queueflow.entity.Clinic;
import com.queueflow.entity.ClinicStaff;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.enums.UserRole;
import com.queueflow.repository.AppUserRepository;
import com.queueflow.repository.ClinicRepository;
import com.queueflow.repository.ClinicStaffRepository;
import com.queueflow.repository.DoctorProfileRepository;

@Service
public class DoctorService {

    private final AppUserRepository appUserRepository;
    private final ClinicRepository clinicRepository;
    private final ClinicStaffRepository clinicStaffRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DoctorService(
            AppUserRepository appUserRepository,
            ClinicRepository clinicRepository,
            ClinicStaffRepository clinicStaffRepository,
            DoctorProfileRepository doctorProfileRepository) {

        this.appUserRepository = appUserRepository;
        this.clinicRepository = clinicRepository;
        this.clinicStaffRepository = clinicStaffRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Transactional
    public DoctorProfile registerDoctor(DoctorRegistrationRequest request) {

        // Check duplicate email

        String email = request.getEmail().trim().toLowerCase();

        if (appUserRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Find clinic

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        // Create user

        AppUser user = new AppUser();

        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole("DOCTOR");

        user = appUserRepository.save(user);

        // Create clinic staff

        ClinicStaff clinicStaff = new ClinicStaff();

        clinicStaff.setUser(user);
        clinicStaff.setClinic(clinic);
        clinicStaff.setRole(UserRole.DOCTOR);

        clinicStaff = clinicStaffRepository.save(clinicStaff);

        // Create doctor profile

        DoctorProfile doctor = new DoctorProfile();

        doctor.setClinicStaff(clinicStaff);
        doctor.setQualification(request.getQualification());
        doctor.setActive(true);
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationDurationMinutes(request.getConsultationDurationMinutes());
        doctor.setConsultationFee(request.getConsultationFee());

        return doctorProfileRepository.save(doctor);
    }
}
