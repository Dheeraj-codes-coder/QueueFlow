
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

import com.queueflow.dto.DoctorUpcomingAppointmentResponse;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.service.DoctorUpcomingAppointmentService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorUpcomingAppointmentController {

    private final DoctorUpcomingAppointmentService doctorUpcomingAppointmentService;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorUpcomingAppointmentController(
            DoctorUpcomingAppointmentService doctorUpcomingAppointmentService,
            DoctorProfileRepository doctorProfileRepository) {

        this.doctorUpcomingAppointmentService = doctorUpcomingAppointmentService;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @GetMapping("/{doctorId}/appointments/upcoming")
    public ResponseEntity<List<DoctorUpcomingAppointmentResponse>>
            getUpcomingAppointments(
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

        List<DoctorUpcomingAppointmentResponse> response =
                doctorUpcomingAppointmentService.getUpcomingAppointments(
                        doctorId, date);

        return ResponseEntity.ok(response);
    }
}
