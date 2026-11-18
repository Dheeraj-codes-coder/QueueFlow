
package com.queueflow.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.AppointmentSlotGenerationResponse;
import com.queueflow.dto.AppointmentSlotResponse;
import com.queueflow.entity.AppointmentSlot;
import com.queueflow.service.AppointmentSlotGenerationService;

@RestController
@RequestMapping("/api/doctors")
public class AppointmentSlotGenerationController {

    private final AppointmentSlotGenerationService appointmentSlotGenerationService;

    public AppointmentSlotGenerationController(
            AppointmentSlotGenerationService appointmentSlotGenerationService) {

        this.appointmentSlotGenerationService = appointmentSlotGenerationService;
    }

    @PostMapping("/{doctorId}/generate-slots")
    public ResponseEntity<AppointmentSlotGenerationResponse> generateSlots(
            @PathVariable Long doctorId) {

        AppointmentSlotGenerationResponse response =
                appointmentSlotGenerationService.generateSlots(doctorId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<AppointmentSlotResponse>> getDoctorSlots(
            @PathVariable Long doctorId) {

        List<AppointmentSlot> slots =
                appointmentSlotGenerationService.getDoctorSlots(doctorId);

        List<AppointmentSlotResponse> responses = new ArrayList<>();

        for (AppointmentSlot slot : slots) {

            AppointmentSlotResponse response = new AppointmentSlotResponse(
                    slot.getId(),
                    slot.getDoctor().getId(),
                    slot.getSlotDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getStatus()
            );

            responses.add(response);
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
