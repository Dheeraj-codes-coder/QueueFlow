
package com.queueflow.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.DoctorDashboardStatisticsResponse;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.QueueTicket;
import com.queueflow.enums.AppointmentStatus;
import com.queueflow.enums.QueueStatus;
import com.queueflow.repository.AppointmentRepository;
import com.queueflow.repository.QueueTicketRepository;

@Service
public class DoctorDashboardStatisticsService {

    private final AppointmentRepository appointmentRepository;
    private final QueueTicketRepository queueTicketRepository;

    public DoctorDashboardStatisticsService(
            AppointmentRepository appointmentRepository,
            QueueTicketRepository queueTicketRepository) {

        this.appointmentRepository = appointmentRepository;
        this.queueTicketRepository = queueTicketRepository;
    }

    @Transactional(readOnly = true)
    public DoctorDashboardStatisticsResponse getDashboardStatistics(
            Long doctorId,
            LocalDate date) {

        List<Appointment> appointments =
                appointmentRepository
                        .findAllByDoctor_IdAndSlot_SlotDateOrderBySlot_StartTimeAsc(
                                doctorId, date);

        long totalAppointments = appointments.size();
        long bookedAppointments = 0;
        long completedAppointments = 0;
        long cancelledAppointments = 0;

        for (Appointment appointment : appointments) {

            if (appointment.getStatus() == AppointmentStatus.BOOKED
                    || appointment.getStatus() == AppointmentStatus.CONFIRMED) {

                bookedAppointments++;
            }

            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                completedAppointments++;
            }

            if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                cancelledAppointments++;
            }
        }

        List<QueueTicket> queueTickets =
                queueTicketRepository
                        .findAllByDoctor_IdAndQueueDateOrderByTokenNumberAsc(
                                doctorId, date);

        long waitingPatients = 0;
        long patientsInConsultation = 0;

        for (QueueTicket ticket : queueTickets) {

            if (ticket.getStatus() == QueueStatus.WAITING) {
                waitingPatients++;
            }

            if (ticket.getStatus() == QueueStatus.IN_CONSULTATION) {
                patientsInConsultation++;
            }
        }

        DoctorDashboardStatisticsResponse response =
                new DoctorDashboardStatisticsResponse(
                        doctorId,
                        totalAppointments,
                        bookedAppointments,
                        completedAppointments,
                        cancelledAppointments,
                        waitingPatients,
                        patientsInConsultation
                );

        return response;
    }
}
