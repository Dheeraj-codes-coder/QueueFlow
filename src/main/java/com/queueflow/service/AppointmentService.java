
package com.queueflow.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.AppointmentCancellationResponse;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.AppointmentSlot;
import com.queueflow.entity.PatientProfile;
import com.queueflow.enums.AppointmentStatus;
import com.queueflow.enums.SlotStatus;
import com.queueflow.repository.AppointmentRepository;
import com.queueflow.repository.AppointmentSlotRepository;
import com.queueflow.repository.PatientProfileRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final PatientProfileRepository patientProfileRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            AppointmentSlotRepository appointmentSlotRepository,
            PatientProfileRepository patientProfileRepository) {

        this.appointmentRepository = appointmentRepository;
        this.appointmentSlotRepository = appointmentSlotRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    @Transactional
    public Appointment bookAppointment(Long patientId, Long slotId, String reason) {

        PatientProfile patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        AppointmentSlot slot = appointmentSlotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new RuntimeException("Appointment slot not found"));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException("This appointment slot is not available");
        }

        if (slot.getSlotDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book an appointment for a past date");
        }

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(slot.getDoctor());
        appointment.setSlot(slot);
        appointment.setReason(reason);
        appointment.setStatus(AppointmentStatus.BOOKED);

        slot.setStatus(SlotStatus.BOOKED);

        appointmentSlotRepository.save(slot);

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public AppointmentCancellationResponse cancelAppointment(
            Long patientId,
            Long appointmentId) {

        Appointment appointment = appointmentRepository
                .findByIdAndPatient_Id(appointmentId, patientId)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found for this patient"));

        if (appointment.getStatus() != AppointmentStatus.BOOKED
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Only booked or confirmed appointments can be cancelled");
        }

        AppointmentSlot slot = appointmentSlotRepository
                .findByIdForUpdate(appointment.getSlot().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Appointment slot not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        slot.setStatus(SlotStatus.AVAILABLE);

        appointmentSlotRepository.save(slot);

        appointmentRepository.save(appointment);

        return new AppointmentCancellationResponse(
                appointment.getId(),
                appointment.getStatus(),
                "Appointment cancelled successfully"
        );
    }

    @Transactional
    public Appointment rescheduleAppointment(
            Long patientId,
            Long appointmentId,
            Long newSlotId) {

        Appointment appointment = appointmentRepository
                .findByIdAndPatientIdForUpdate(appointmentId, patientId)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found for this patient"));

        if (appointment.getStatus() != AppointmentStatus.BOOKED
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Only booked or confirmed appointments can be rescheduled");
        }

        AppointmentSlot oldSlot = appointmentSlotRepository
                .findByIdForUpdate(appointment.getSlot().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Old appointment slot not found"));

        AppointmentSlot newSlot = appointmentSlotRepository
                .findByIdForUpdate(newSlotId)
                .orElseThrow(() -> new RuntimeException(
                        "New appointment slot not found"));

        if (oldSlot.getId().equals(newSlot.getId())) {
            throw new RuntimeException(
                    "Please select a different appointment slot");
        }

        if (newSlot.getStatus() != SlotStatus.AVAILABLE) {
            throw new RuntimeException(
                    "New appointment slot is not available");
        }

        if (newSlot.getSlotDate().isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Cannot reschedule to a past date");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        oldSlot.setStatus(SlotStatus.AVAILABLE);

        Appointment newAppointment = new Appointment();

        newAppointment.setPatient(appointment.getPatient());
        newAppointment.setDoctor(newSlot.getDoctor());
        newAppointment.setSlot(newSlot);
        newAppointment.setReason(appointment.getReason());
        newAppointment.setStatus(AppointmentStatus.BOOKED);

        newSlot.setStatus(SlotStatus.BOOKED);

        appointmentSlotRepository.save(oldSlot);
        appointmentSlotRepository.save(newSlot);
        appointmentRepository.save(appointment);

        return appointmentRepository.save(newAppointment);
    }
}
