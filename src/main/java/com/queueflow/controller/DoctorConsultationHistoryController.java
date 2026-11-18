
package com.queueflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.DoctorConsultationHistoryResponse;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.service.DoctorConsultationHistoryService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorConsultationHistoryController {

    private final DoctorConsultationHistoryService doctorConsultationHistoryService;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorConsultationHistoryController(
            DoctorConsultationHistoryService doctorConsultationHistoryService,
            DoctorProfileRepository doctorProfileRepository) {

        this.doctorConsultationHistoryService = doctorConsultationHistoryService;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @GetMapping("/{doctorId}/consultations/history")
    public ResponseEntity<List<DoctorConsultationHistoryResponse>>
            getDoctorConsultationHistory(@PathVariable Long doctorId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isOwner =
                doctorProfileRepository
                        .existsByIdAndClinicStaff_User_Email(doctorId, email);

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this doctor's consultation history");
        }

        List<DoctorConsultationHistoryResponse> response =
                doctorConsultationHistoryService
                        .getDoctorConsultationHistory(doctorId);

        return ResponseEntity.ok(response);
    }
}
