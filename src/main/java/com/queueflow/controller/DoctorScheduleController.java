
package com.queueflow.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.queueflow.service.DoctorScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    public DoctorScheduleController(DoctorScheduleService doctorScheduleService) {
        this.doctorScheduleService = doctorScheduleService;
    }

    @PostMapping("/{doctorId}/schedule")
    public ResponseEntity<DoctorScheduleResponse> createSchedule(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorScheduleRequest request) {

        DoctorSchedule schedule = doctorScheduleService.createSchedule(doctorId, request);

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

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/schedule/{scheduleId}")
    public ResponseEntity<DoctorScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody DoctorScheduleUpdateRequest request) {

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

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable Long scheduleId) {

        doctorScheduleService.deleteSchedule(scheduleId);

        return new ResponseEntity<>(
                "Doctor schedule deleted successfully",
                HttpStatus.OK
        );
    }
}
