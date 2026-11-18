
package com.queueflow.controller;

import java.time.LocalDate;

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

import com.queueflow.dto.DoctorDashboardStatisticsResponse;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.service.DoctorDashboardStatisticsService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorDashboardStatisticsController {

    private final DoctorDashboardStatisticsService doctorDashboardStatisticsService;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorDashboardStatisticsController(
            DoctorDashboardStatisticsService doctorDashboardStatisticsService,
            DoctorProfileRepository doctorProfileRepository) {

        this.doctorDashboardStatisticsService = doctorDashboardStatisticsService;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @GetMapping("/{doctorId}/dashboard/statistics")
    public ResponseEntity<DoctorDashboardStatisticsResponse>
            getDashboardStatistics(
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
                    "You are not allowed to access this doctor's dashboard");
        }

        DoctorDashboardStatisticsResponse response =
                doctorDashboardStatisticsService.getDashboardStatistics(
                        doctorId, date);

        return ResponseEntity.ok(response);
    }
}
