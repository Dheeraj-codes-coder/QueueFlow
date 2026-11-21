
package com.queueflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.DoctorConsultationHistoryResponse;
import com.queueflow.entity.Consultation;
import com.queueflow.entity.QueueTicket;
import com.queueflow.entity.PatientProfile;
import com.queueflow.entity.AppUser;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.AppointmentSlot;
import com.queueflow.repository.ConsultationRepository;
import com.queueflow.repository.PrescriptionRepository;

@Service
public class DoctorConsultationHistoryService {

    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DoctorConsultationHistoryService(
            ConsultationRepository consultationRepository,
            PrescriptionRepository prescriptionRepository) {

        this.consultationRepository = consultationRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorConsultationHistoryResponse> getDoctorConsultationHistory(
            Long doctorId) {

        List<Consultation> consultations =
                consultationRepository.findAllByDoctor_IdOrderByStartedAtDesc(
                        doctorId);

        List<DoctorConsultationHistoryResponse> responses =
                new ArrayList<>();

        for (Consultation consultation : consultations) {

            QueueTicket queueTicket = consultation.getQueueTicket();

            PatientProfile patient = queueTicket.getPatient();

            AppUser user = patient.getUser();

            Appointment appointment = queueTicket.getAppointment();

            AppointmentSlot slot = appointment.getSlot();

            Long prescriptionId = prescriptionRepository
                    .findByConsultation_Id(consultation.getId())
                    .map(prescription -> prescription.getId())
                    .orElse(null);

            DoctorConsultationHistoryResponse response =
                    new DoctorConsultationHistoryResponse(
                            consultation.getId(),
                            prescriptionId,
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
                            consultation.getStartedAt(),
                            consultation.getEndedAt(),
                            consultation.getNotes()
                    );

            responses.add(response);
        }

        return responses;
    }
}
