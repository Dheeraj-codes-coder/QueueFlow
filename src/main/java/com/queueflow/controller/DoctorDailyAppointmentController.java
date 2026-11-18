
package com.queueflow.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.DoctorDailyAppointmentResponse;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.service.DoctorDailyAppointmentService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorDailyAppointmentController {

    private final DoctorDailyAppointmentService doctorDailyAppointmentService;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorDailyAppointmentController(
            DoctorDailyAppointmentService doctorDailyAppointmentService,
            DoctorProfileRepository doctorProfileRepository) {

        this.doctorDailyAppointmentService = doctorDailyAppointmentService;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @GetMapping("/{doctorId}/appointments/daily")
    public ResponseEntity<List<DoctorDailyAppointmentResponse>>
            getDailyAppointments(
                    @PathVariable Long doctorId,
                    @RequestParam
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate date) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isOwner =
                doctorProfileRepository
                        .existsByIdAndClinicStaff_User_Email(doctorId, email);

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this doctor's appointments");
        }

        List<DoctorDailyAppointmentResponse> response =
                doctorDailyAppointmentService.getDailyAppointments(
                        doctorId, date);

        return ResponseEntity.ok(response);
    }
}
