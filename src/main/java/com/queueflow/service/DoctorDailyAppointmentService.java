
package com.queueflow.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.DoctorDailyAppointmentResponse;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.AppointmentSlot;
import com.queueflow.entity.PatientProfile;
import com.queueflow.entity.AppUser;
import com.queueflow.repository.AppointmentRepository;

@Service
public class DoctorDailyAppointmentService {

    private final AppointmentRepository appointmentRepository;

    public DoctorDailyAppointmentService(
            AppointmentRepository appointmentRepository) {

        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorDailyAppointmentResponse> getDailyAppointments(
            Long doctorId,
            LocalDate appointmentDate) {

        List<Appointment> appointments =
                appointmentRepository
                        .findAllByDoctor_IdAndSlot_SlotDateOrderBySlot_StartTimeAsc(
                                doctorId, appointmentDate);

        List<DoctorDailyAppointmentResponse> responses =
                new ArrayList<>();

        for (Appointment appointment : appointments) {

            PatientProfile patient = appointment.getPatient();

            AppUser user = patient.getUser();

            AppointmentSlot slot = appointment.getSlot();

            DoctorDailyAppointmentResponse response =
                    new DoctorDailyAppointmentResponse(
                            appointment.getId(),
                            patient.getId(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhone(),
                            slot.getSlotDate(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            appointment.getStatus(),
                            appointment.getReason(),
                            appointment.getBookedAt()
                    );

            responses.add(response);
        }

        return responses;
    }
}
