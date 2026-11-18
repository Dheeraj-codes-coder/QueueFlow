
package com.queueflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.queueflow.dto.PatientAppointmentResponse;
import com.queueflow.entity.Appointment;
import com.queueflow.repository.AppointmentRepository;
import com.queueflow.repository.PatientProfileRepository;

@Service
public class PatientAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientProfileRepository patientProfileRepository;

    public PatientAppointmentService(
            AppointmentRepository appointmentRepository,
            PatientProfileRepository patientProfileRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    public List<PatientAppointmentResponse> getPatientAppointments(Long patientId) {

        patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<Appointment> appointments =
                appointmentRepository
                        .findAllByPatient_IdOrderByBookedAtDesc(patientId);

        List<PatientAppointmentResponse> responses = new ArrayList<>();

        for (Appointment appointment : appointments) {

            PatientAppointmentResponse response =
                    new PatientAppointmentResponse(
                            appointment.getId(),
                            appointment.getDoctor().getId(),
                            appointment.getDoctor().getClinicStaff().getUser().getFullName(),
                            appointment.getDoctor().getClinicStaff().getClinic().getName(),
                            appointment.getSlot().getSlotDate(),
                            appointment.getSlot().getStartTime(),
                            appointment.getSlot().getEndTime(),
                            appointment.getStatus(),
                            appointment.getReason(),
                            appointment.getBookedAt()
                    );

            responses.add(response);
        }

        return responses;
    }
}
