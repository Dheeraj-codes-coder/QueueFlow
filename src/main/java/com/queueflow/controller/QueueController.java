package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.ConsultationCompleteRequest;
import com.queueflow.dto.ConsultationCompleteResponse;
import com.queueflow.dto.ConsultationStartResponse;
import com.queueflow.dto.QueueCallResponse;
import com.queueflow.dto.QueueCheckInRequest;
import com.queueflow.dto.QueueCheckInResponse;
import com.queueflow.dto.QueueTicketResponse;
import com.queueflow.repository.AppointmentRepository;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.repository.QueueTicketRepository;
import com.queueflow.service.QueueTicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueTicketService queueTicketService;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final QueueTicketRepository queueTicketRepository;

    public QueueController(
            QueueTicketService queueTicketService,
            DoctorProfileRepository doctorProfileRepository,
            AppointmentRepository appointmentRepository,
            QueueTicketRepository queueTicketRepository) {

        this.queueTicketService = queueTicketService;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.queueTicketRepository = queueTicketRepository;
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
                    "You are not allowed to manage this doctor's queue");
        }
    }

    private void checkAppointmentOwnership(Long appointmentId) {

        boolean isOwner = appointmentRepository
                .existsByIdAndDoctor_ClinicStaff_User_Email(
                        appointmentId, getLoggedInEmail());

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to check in this appointment");
        }
    }

    private void checkQueueTicketOwnership(Long queueTicketId) {

        boolean isOwner = queueTicketRepository
                .existsByIdAndDoctor_ClinicStaff_User_Email(
                        queueTicketId, getLoggedInEmail());

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to manage this queue ticket");
        }
    }

    @PostMapping("/check-in")
    public ResponseEntity<QueueCheckInResponse> checkInPatient(
            @Valid @RequestBody QueueCheckInRequest request) {

        checkAppointmentOwnership(request.getAppointmentId());

        QueueCheckInResponse response =
                queueTicketService.checkInPatient(
                        request.getAppointmentId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/doctors/{doctorId}/today")
    public ResponseEntity<List<QueueTicketResponse>> getTodayQueue(
            @PathVariable Long doctorId) {

        checkDoctorOwnership(doctorId);

        List<QueueTicketResponse> response =
                queueTicketService.getTodayQueue(doctorId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/doctors/{doctorId}/call-next")
    public ResponseEntity<QueueCallResponse> callNextPatient(
            @PathVariable Long doctorId) {

        checkDoctorOwnership(doctorId);

        QueueCallResponse response =
                queueTicketService.callNextPatient(doctorId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{queueTicketId}/start-consultation")
    public ResponseEntity<ConsultationStartResponse> startConsultation(
            @PathVariable Long queueTicketId) {

        checkQueueTicketOwnership(queueTicketId);

        ConsultationStartResponse response =
                queueTicketService.startConsultation(queueTicketId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{queueTicketId}/complete-consultation")
    public ResponseEntity<ConsultationCompleteResponse> completeConsultation(
            @PathVariable Long queueTicketId,
            @RequestBody ConsultationCompleteRequest request) {

        checkQueueTicketOwnership(queueTicketId);

        ConsultationCompleteResponse response =
                queueTicketService.completeConsultation(
                        queueTicketId,
                        request.getNotes());

        return ResponseEntity.ok(response);
    }
}