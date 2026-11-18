
package com.queueflow.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.ConsultationCompleteResponse;
import com.queueflow.dto.ConsultationStartResponse;
import com.queueflow.dto.QueueCallResponse;
import com.queueflow.dto.QueueCheckInResponse;
import com.queueflow.dto.QueueTicketResponse;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.Consultation;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.entity.QueueTicket;
import com.queueflow.enums.AppointmentStatus;
import com.queueflow.enums.QueueStatus;
import com.queueflow.enums.SlotStatus;
import com.queueflow.repository.AppointmentRepository;
import com.queueflow.repository.AppointmentSlotRepository;
import com.queueflow.repository.ConsultationRepository;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.repository.QueueTicketRepository;

@Service
public class QueueTicketService {

    private final QueueTicketRepository queueTicketRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final ConsultationRepository consultationRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;

    public QueueTicketService(
            QueueTicketRepository queueTicketRepository,
            AppointmentRepository appointmentRepository,
            DoctorProfileRepository doctorProfileRepository,
            ConsultationRepository consultationRepository,
            AppointmentSlotRepository appointmentSlotRepository) {

        this.queueTicketRepository = queueTicketRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.consultationRepository = consultationRepository;
        this.appointmentSlotRepository = appointmentSlotRepository;
    }

    @Transactional
    public QueueCheckInResponse checkInPatient(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.BOOKED
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Only booked or confirmed appointments can check in");
        }

        if (!appointment.getSlot().getSlotDate().isEqual(LocalDate.now())) {
            throw new RuntimeException(
                    "Patient can check in only on the appointment date");
        }

        DoctorProfile doctor = doctorProfileRepository
                .findByIdForUpdate(appointment.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (queueTicketRepository.existsByAppointment_Id(appointmentId)) {
            throw new RuntimeException(
                    "Patient has already checked in for this appointment");
        }

        Integer lastTokenNumber = queueTicketRepository.findMaximumTokenNumber(
                doctor.getId(),
                LocalDate.now()
        );

        int nextTokenNumber = lastTokenNumber + 1;

        QueueTicket queueTicket = new QueueTicket();

        queueTicket.setPatient(appointment.getPatient());
        queueTicket.setDoctor(doctor);
        queueTicket.setAppointment(appointment);
        queueTicket.setTokenNumber(nextTokenNumber);
        queueTicket.setQueueDate(LocalDate.now());
        queueTicket.setCheckInTime(LocalDateTime.now());
        queueTicket.setStatus(QueueStatus.WAITING);

        QueueTicket savedTicket = queueTicketRepository.save(queueTicket);

        return new QueueCheckInResponse(
                savedTicket.getId(),
                appointment.getId(),
                appointment.getPatient().getId(),
                doctor.getId(),
                savedTicket.getTokenNumber(),
                savedTicket.getQueueDate(),
                savedTicket.getStatus(),
                "Patient checked in successfully"
        );
    }

    public List<QueueTicketResponse> getTodayQueue(Long doctorId) {

        doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<QueueTicket> queueTickets =
                queueTicketRepository.findAllByDoctor_IdAndQueueDateOrderByTokenNumberAsc(
                        doctorId,
                        LocalDate.now()
                );

        List<QueueTicketResponse> responses = new ArrayList<>();

        for (QueueTicket ticket : queueTickets) {

            QueueTicketResponse response = new QueueTicketResponse(
                    ticket.getId(),
                    ticket.getTokenNumber(),
                    ticket.getPatient().getId(),
                    ticket.getPatient().getUser().getFullName(),
                    ticket.getDoctor().getId(),
                    ticket.getQueueDate(),
                    ticket.getCheckInTime(),
                    ticket.getStatus()
            );

            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public QueueCallResponse callNextPatient(Long doctorId) {

        DoctorProfile doctor = doctorProfileRepository
                .findByIdForUpdate(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        boolean activePatient = queueTicketRepository
                .existsByDoctor_IdAndQueueDateAndStatusIn(
                        doctor.getId(),
                        LocalDate.now(),
                        List.of(
                                QueueStatus.CALLED,
                                QueueStatus.IN_CONSULTATION
                        )
                );

        if (activePatient) {
            throw new RuntimeException(
                    "Doctor already has an active patient");
        }

        QueueTicket ticket = queueTicketRepository
                .findFirstByDoctor_IdAndQueueDateAndStatusOrderByTokenNumberAsc(
                        doctor.getId(),
                        LocalDate.now(),
                        QueueStatus.WAITING
                )
                .orElseThrow(() -> new RuntimeException(
                        "No waiting patients in the queue"));

        ticket.setStatus(QueueStatus.CALLED);
        ticket.setCalledTime(LocalDateTime.now());

        QueueTicket savedTicket = queueTicketRepository.save(ticket);

        return new QueueCallResponse(
                savedTicket.getId(),
                savedTicket.getTokenNumber(),
                savedTicket.getPatient().getUser().getFullName(),
                savedTicket.getStatus(),
                "Patient called successfully"
        );
    }

    @Transactional
    public ConsultationStartResponse startConsultation(Long queueTicketId) {

        QueueTicket ticket = queueTicketRepository
                .findByIdForUpdate(queueTicketId)
                .orElseThrow(() -> new RuntimeException(
                        "Queue ticket not found"));

        if (ticket.getStatus() != QueueStatus.CALLED) {
            throw new RuntimeException(
                    "Only called patients can start consultation");
        }

        if (consultationRepository.findByQueueTicket_Id(queueTicketId).isPresent()) {
            throw new RuntimeException(
                    "Consultation already exists for this patient");
        }

        LocalDateTime startTime = LocalDateTime.now();

        Consultation consultation = new Consultation();

        consultation.setQueueTicket(ticket);
        consultation.setDoctor(ticket.getDoctor());
        consultation.setStartedAt(startTime);

        ticket.setStatus(QueueStatus.IN_CONSULTATION);

        Consultation savedConsultation =
                consultationRepository.save(consultation);

        queueTicketRepository.save(ticket);

        return new ConsultationStartResponse(
                savedConsultation.getId(),
                ticket.getId(),
                ticket.getTokenNumber(),
                ticket.getPatient().getUser().getFullName(),
                ticket.getStatus(),
                savedConsultation.getStartedAt(),
                "Consultation started successfully"
        );
    }

    @Transactional
    public ConsultationCompleteResponse completeConsultation(
            Long queueTicketId,
            String notes) {

        QueueTicket ticket = queueTicketRepository
                .findByIdForUpdate(queueTicketId)
                .orElseThrow(() -> new RuntimeException(
                        "Queue ticket not found"));

        if (ticket.getStatus() != QueueStatus.IN_CONSULTATION) {
            throw new RuntimeException(
                    "Only patients in consultation can be completed");
        }

        doctorProfileRepository.findByIdForUpdate(ticket.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Consultation consultation = consultationRepository
                .findByQueueTicket_Id(queueTicketId)
                .orElseThrow(() -> new RuntimeException(
                        "Consultation not found"));

        LocalDateTime endTime = LocalDateTime.now();

        consultation.setNotes(notes);
        consultation.setEndedAt(endTime);

        ticket.setStatus(QueueStatus.COMPLETED);

        Appointment appointment = ticket.getAppointment();

        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);

            appointment.getSlot().setStatus(SlotStatus.BOOKED);
            appointmentSlotRepository.save(appointment.getSlot());
        }

        consultationRepository.save(consultation);
        queueTicketRepository.save(ticket);

        return new ConsultationCompleteResponse(
                consultation.getId(),
                ticket.getId(),
                ticket.getTokenNumber(),
                ticket.getPatient().getUser().getFullName(),
                ticket.getStatus(),
                appointment != null
                        ? appointment.getStatus()
                        : null,
                endTime,
                "Consultation completed successfully"
        );
    }
}
