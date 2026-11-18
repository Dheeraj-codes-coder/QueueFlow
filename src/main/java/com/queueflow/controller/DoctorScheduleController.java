package com.queueflow.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.DoctorScheduleRequest;
import com.queueflow.dto.DoctorScheduleResponse;
import com.queueflow.dto.DoctorScheduleUpdateRequest;
import com.queueflow.entity.DoctorSchedule;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.repository.DoctorScheduleRepository;
import com.queueflow.service.DoctorScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public DoctorScheduleController(
            DoctorScheduleService doctorScheduleService,
            DoctorProfileRepository doctorProfileRepository,
            DoctorScheduleRepository doctorScheduleRepository) {

        this.doctorScheduleService = doctorScheduleService;
        this.doctorProfileRepository = doctorProfileRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    private String getLoggedInEmail() {

        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    private void checkDoctorOwnership(Long doctorId) {

        boolean isOwner = doctorProfileRepository
                .existsByIdAndClinicStaff_User_Email(
                        doctorId, getLoggedInEmail());

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this doctor's schedule");
        }
    }

    private void checkScheduleOwnership(Long scheduleId) {

        boolean isOwner = doctorScheduleRepository
                .existsByIdAndDoctor_ClinicStaff_User_Email(
                        scheduleId, getLoggedInEmail());

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this doctor's schedule");
        }
    }

    @PostMapping("/{doctorId}/schedule")
    public ResponseEntity<DoctorScheduleResponse> createSchedule(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorScheduleRequest request) {

        checkDoctorOwnership(doctorId);

        DoctorSchedule schedule =
                doctorScheduleService.createSchedule(doctorId, request);

        DoctorScheduleResponse response = new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.isAvailable()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}/schedule")
    public ResponseEntity<List<DoctorScheduleResponse>> getDoctorSchedule(
            @PathVariable Long doctorId) {

        List<DoctorSchedule> schedules =
                doctorScheduleService.getDoctorSchedule(doctorId);

        List<DoctorScheduleResponse> responses = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {

            DoctorScheduleResponse response = new DoctorScheduleResponse(
                    schedule.getId(),
                    schedule.getDoctor().getId(),
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.isAvailable()
            );

            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/schedule/{scheduleId}")
    public ResponseEntity<DoctorScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody DoctorScheduleUpdateRequest request) {

        checkScheduleOwnership(scheduleId);

        DoctorSchedule schedule =
                doctorScheduleService.updateSchedule(scheduleId, request);

        DoctorScheduleResponse response = new DoctorScheduleResponse(
                schedule.getId(),
                schedule.getDoctor().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.isAvailable()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable Long scheduleId) {

        checkScheduleOwnership(scheduleId);

        doctorScheduleService.deleteSchedule(scheduleId);

        return ResponseEntity.ok(
                "Doctor schedule deleted successfully");
    }
}