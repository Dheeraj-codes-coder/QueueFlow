package com.queueflow.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.PrescriptionCreateRequest;
import com.queueflow.dto.PrescriptionDetailsResponse;
import com.queueflow.dto.PrescriptionItemResponse;
import com.queueflow.dto.PrescriptionResponse;
import com.queueflow.dto.PatientPrescriptionHistoryResponse;
import com.queueflow.entity.Consultation;
import com.queueflow.entity.Prescription;
import com.queueflow.entity.PrescriptionItem;
import com.queueflow.repository.ConsultationRepository;
import com.queueflow.repository.PrescriptionItemRepository;
import com.queueflow.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            ConsultationRepository consultationRepository,
            PrescriptionItemRepository prescriptionItemRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
    }

    @Transactional
    public PrescriptionResponse createPrescription(
            PrescriptionCreateRequest request) {

        Consultation consultation = consultationRepository
                .findById(request.getConsultationId())
                .orElseThrow(() -> new RuntimeException(
                        "Consultation not found"));

        if (consultation.getEndedAt() == null) {
            throw new RuntimeException(
                    "Prescription can only be created for a completed consultation");
        }

        if (prescriptionRepository
                .findByConsultation_Id(consultation.getId()).isPresent()) {

            throw new RuntimeException(
                    "Prescription already exists for this consultation");
        }

        Prescription prescription = new Prescription();

        prescription.setConsultation(consultation);
        prescription.setPrescribedAt(LocalDateTime.now());
        prescription.setInstructions(request.getInstructions());

        Prescription savedPrescription =
                prescriptionRepository.save(prescription);

        return new PrescriptionResponse(
                savedPrescription.getId(),
                consultation.getId(),
                savedPrescription.getPrescribedAt(),
                savedPrescription.getInstructions(),
                "Prescription created successfully"
        );
    }

    public PrescriptionDetailsResponse getPrescriptionDetails(
            Long prescriptionId) {

        Prescription prescription = prescriptionRepository
                .findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException(
                        "Prescription not found"));

        List<PrescriptionItem> items =
                prescriptionItemRepository.findAllByPrescription_Id(
                        prescriptionId);

        List<PrescriptionItemResponse> medicines = new ArrayList<>();

        for (PrescriptionItem item : items) {

            PrescriptionItemResponse response =
                    new PrescriptionItemResponse(
                            item.getId(),
                            prescriptionId,
                            item.getMedicineName(),
                            item.getDosage(),
                            item.getFrequency(),
                            item.getDurationDays(),
                            item.getQuantity(),
                            item.getInstructions(),
                            "Medicine retrieved successfully"
                    );

            medicines.add(response);
        }

        return new PrescriptionDetailsResponse(
                prescription.getId(),
                prescription.getConsultation().getId(),
                prescription.getPrescribedAt(),
                prescription.getInstructions(),
                medicines
        );
    }

    @Transactional(readOnly = true)
    public List<PatientPrescriptionHistoryResponse> getPatientPrescriptionHistory(
            Long patientId) {

        List<Prescription> prescriptions =
                prescriptionRepository
                        .findAllByConsultation_QueueTicket_Patient_IdOrderByPrescribedAtDesc(
                                patientId);

        List<PatientPrescriptionHistoryResponse> responses =
                new ArrayList<>();

        for (Prescription prescription : prescriptions) {

            Consultation consultation = prescription.getConsultation();

            List<PrescriptionItem> items =
                    prescriptionItemRepository.findAllByPrescription_Id(
                            prescription.getId());

            List<PrescriptionItemResponse> medicines =
                    new ArrayList<>();

            for (PrescriptionItem item : items) {

                PrescriptionItemResponse medicine =
                        new PrescriptionItemResponse(
                                item.getId(),
                                prescription.getId(),
                                item.getMedicineName(),
                                item.getDosage(),
                                item.getFrequency(),
                                item.getDurationDays(),
                                item.getQuantity(),
                                item.getInstructions(),
                                "Medicine retrieved successfully"
                        );

                medicines.add(medicine);
            }

            PatientPrescriptionHistoryResponse response =
                    new PatientPrescriptionHistoryResponse(
                            prescription.getId(),
                            consultation.getId(),
                            consultation.getStartedAt(),
                            consultation.getEndedAt(),
                            consultation.getNotes(),
                            prescription.getPrescribedAt(),
                            prescription.getInstructions(),
                            medicines
                    );

            responses.add(response);
        }

        return responses;
    }
}