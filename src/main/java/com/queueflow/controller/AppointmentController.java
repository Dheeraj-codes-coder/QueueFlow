
package com.queueflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.AppointmentBookingRequest;
import com.queueflow.dto.AppointmentBookingResponse;
import com.queueflow.dto.AppointmentCancellationResponse;
import com.queueflow.dto.AppointmentRescheduleRequest;
import com.queueflow.entity.Appointment;
import com.queueflow.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentBookingResponse> bookAppointment(
            @Valid @RequestBody AppointmentBookingRequest request) {

        Appointment appointment = appointmentService.bookAppointment(
                request.getPatientId(),
                request.getSlotId(),
                request.getReason()
        );

        AppointmentBookingResponse response = new AppointmentBookingResponse(
                appointment.getId(),
                appointment.getSlot().getId(),
                appointment.getStatus(),
                "Appointment booked successfully"
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentCancellationResponse> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long patientId) {

        AppointmentCancellationResponse response =
                appointmentService.cancelAppointment(patientId, appointmentId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentBookingResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRescheduleRequest request) {

        Appointment appointment = appointmentService.rescheduleAppointment(
                request.getPatientId(),
                appointmentId,
                request.getNewSlotId()
        );

        AppointmentBookingResponse response = new AppointmentBookingResponse(
                appointment.getId(),
                appointment.getSlot().getId(),
                appointment.getStatus(),
                "Appointment rescheduled successfully"
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
