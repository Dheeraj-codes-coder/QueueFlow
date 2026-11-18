
package com.queueflow.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.PatientUpcomingAppointmentResponse;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.AppointmentSlot;
import com.queueflow.entity.AppUser;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.enums.AppointmentStatus;
import com.queueflow.repository.AppointmentRepository;

@Service
public class PatientUpcomingAppointmentService {

    private final AppointmentRepository appointmentRepository;

    public PatientUpcomingAppointmentService(
            AppointmentRepository appointmentRepository) {

        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientUpcomingAppointmentResponse> getUpcomingAppointments(
            Long patientId) {

        List<AppointmentStatus> statuses = Arrays.asList(
                AppointmentStatus.BOOKED,
                AppointmentStatus.CONFIRMED
        );

        List<Appointment> appointments =
                appointmentRepository
                        .findAllByPatient_IdAndSlot_SlotDateGreaterThanEqualAndStatusInOrderBySlot_SlotDateAscSlot_StartTimeAsc(
                                patientId, LocalDate.now(), statuses);

        List<PatientUpcomingAppointmentResponse> responses =
                new ArrayList<>();

        for (Appointment appointment : appointments) {

            DoctorProfile doctor = appointment.getDoctor();

            AppUser user = doctor.getClinicStaff().getUser();

            AppointmentSlot slot = appointment.getSlot();

            PatientUpcomingAppointmentResponse response =
                    new PatientUpcomingAppointmentResponse(
                            appointment.getId(),
                            doctor.getId(),
                            user.getFullName(),
                            doctor.getClinicStaff().getClinic().getName(),
                            slot.getSlotDate(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            appointment.getStatus(),
                            appointment.getReason()
                    );

            responses.add(response);
        }

        return responses;
    }
}
