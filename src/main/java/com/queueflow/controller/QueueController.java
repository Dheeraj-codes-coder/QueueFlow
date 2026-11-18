
package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.queueflow.service.QueueTicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueTicketService queueTicketService;

    public QueueController(QueueTicketService queueTicketService) {
        this.queueTicketService = queueTicketService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<QueueCheckInResponse> checkInPatient(
            @Valid @RequestBody QueueCheckInRequest request) {

        QueueCheckInResponse response = queueTicketService.checkInPatient(
                request.getAppointmentId()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/doctors/{doctorId}/today")
    public ResponseEntity<List<QueueTicketResponse>> getTodayQueue(
            @PathVariable Long doctorId) {

        List<QueueTicketResponse> response =
                queueTicketService.getTodayQueue(doctorId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/doctors/{doctorId}/call-next")
    public ResponseEntity<QueueCallResponse> callNextPatient(
            @PathVariable Long doctorId) {

        QueueCallResponse response =
                queueTicketService.callNextPatient(doctorId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{queueTicketId}/start-consultation")
    public ResponseEntity<ConsultationStartResponse> startConsultation(
            @PathVariable Long queueTicketId) {

        ConsultationStartResponse response =
                queueTicketService.startConsultation(queueTicketId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{queueTicketId}/complete-consultation")
    public ResponseEntity<ConsultationCompleteResponse> completeConsultation(
            @PathVariable Long queueTicketId,
            @RequestBody ConsultationCompleteRequest request) {

        ConsultationCompleteResponse response =
                queueTicketService.completeConsultation(
                        queueTicketId,
                        request.getNotes()
                );

        return ResponseEntity.ok(response);
    }
}
