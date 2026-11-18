package com.queueflow.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.PatientDashboardResponse;
import com.queueflow.entity.PatientProfile;
import com.queueflow.entity.AppUser;
import com.queueflow.entity.Appointment;
import com.queueflow.enums.AppointmentStatus;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.repository.AppointmentRepository;

@Service
public class PatientDashboardService {

    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientDashboardService(
            PatientProfileRepository patientProfileRepository,
            AppointmentRepository appointmentRepository) {

        this.patientProfileRepository = patientProfileRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public PatientDashboardResponse getPatientDashboard(Long patientId) {

        PatientProfile patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        AppUser user = patient.getUser();

        List<Appointment> appointments =
                appointmentRepository
                        .findAllByPatient_IdOrderByBookedAtDesc(patientId);

        int totalAppointments = appointments.size();
        int upcomingAppointments = 0;
        int completedAppointments = 0;
        int cancelledAppointments = 0;

        for (Appointment appointment : appointments) {

            if (appointment.getStatus() == AppointmentStatus.BOOKED
                    || appointment.getStatus() == AppointmentStatus.CONFIRMED) {

                if (!appointment.getSlot().getSlotDate().isBefore(LocalDate.now())) {
                    upcomingAppointments++;
                }
            }

            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                completedAppointments++;
            }

            if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                cancelledAppointments++;
            }
        }

        PatientDashboardResponse response =
                new PatientDashboardResponse(
                        patient.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        totalAppointments,
                        upcomingAppointments,
                        completedAppointments,
                        cancelledAppointments
                );

        return response;
    }
}