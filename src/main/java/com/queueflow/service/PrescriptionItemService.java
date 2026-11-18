package com.queueflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.PrescriptionItemRequest;
import com.queueflow.dto.PrescriptionItemResponse;
import com.queueflow.entity.Prescription;
import com.queueflow.entity.PrescriptionItem;
import com.queueflow.repository.PrescriptionItemRepository;
import com.queueflow.repository.PrescriptionRepository;

@Service
public class PrescriptionItemService {

    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionItemService(
            PrescriptionItemRepository prescriptionItemRepository,
            PrescriptionRepository prescriptionRepository) {

        this.prescriptionItemRepository = prescriptionItemRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public PrescriptionItemResponse addMedicine(
            Long prescriptionId,
            PrescriptionItemRequest request) {

        Prescription prescription = prescriptionRepository
                .findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException(
                        "Prescription not found"));

        PrescriptionItem item = new PrescriptionItem();

        item.setPrescription(prescription);
        item.setMedicineName(request.getMedicineName());
        item.setDosage(request.getDosage());
        item.setFrequency(request.getFrequency());
        item.setDurationDays(request.getDurationDays());
        item.setQuantity(request.getQuantity());
        item.setInstructions(request.getInstructions());

        PrescriptionItem savedItem =
                prescriptionItemRepository.save(item);

        return new PrescriptionItemResponse(
                savedItem.getId(),
                prescription.getId(),
                savedItem.getMedicineName(),
                savedItem.getDosage(),
                savedItem.getFrequency(),
                savedItem.getDurationDays(),
                savedItem.getQuantity(),
                savedItem.getInstructions(),
                "Medicine added successfully"
        );
    }

    public List<PrescriptionItemResponse> getPrescriptionMedicines(
            Long prescriptionId) {

        prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException(
                        "Prescription not found"));

        List<PrescriptionItem> items =
                prescriptionItemRepository.findAllByPrescription_Id(
                        prescriptionId
                );

        List<PrescriptionItemResponse> responses = new ArrayList<>();

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

            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public PrescriptionItemResponse updateMedicine(
            Long prescriptionId,
            Long prescriptionItemId,
            PrescriptionItemRequest request) {

        PrescriptionItem item = prescriptionItemRepository
                .findById(prescriptionItemId)
                .orElseThrow(() -> new RuntimeException(
                        "Medicine not found"));

        if (!item.getPrescription().getId().equals(prescriptionId)) {
            throw new RuntimeException(
                    "Medicine does not belong to this prescription");
        }

        item.setMedicineName(request.getMedicineName());
        item.setDosage(request.getDosage());
        item.setFrequency(request.getFrequency());
        item.setDurationDays(request.getDurationDays());
        item.setQuantity(request.getQuantity());
        item.setInstructions(request.getInstructions());

        PrescriptionItem updatedItem =
                prescriptionItemRepository.save(item);

        return new PrescriptionItemResponse(
                updatedItem.getId(),
                prescriptionId,
                updatedItem.getMedicineName(),
                updatedItem.getDosage(),
                updatedItem.getFrequency(),
                updatedItem.getDurationDays(),
                updatedItem.getQuantity(),
                updatedItem.getInstructions(),
                "Medicine updated successfully"
        );
    }

    @Transactional
    public String deleteMedicine(
            Long prescriptionId,
            Long prescriptionItemId) {

        PrescriptionItem item = prescriptionItemRepository
                .findById(prescriptionItemId)
                .orElseThrow(() -> new RuntimeException(
                        "Medicine not found"));

        if (!item.getPrescription().getId().equals(prescriptionId)) {
            throw new RuntimeException(
                    "Medicine does not belong to this prescription");
        }

        prescriptionItemRepository.delete(item);

        return "Medicine deleted successfully";
    }
}