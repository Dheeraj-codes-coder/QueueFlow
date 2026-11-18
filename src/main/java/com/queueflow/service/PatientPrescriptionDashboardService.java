
package com.queueflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.PatientPrescriptionDashboardResponse;
import com.queueflow.entity.Prescription;
import com.queueflow.entity.PrescriptionItem;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.entity.AppUser;
import com.queueflow.repository.PrescriptionRepository;
import com.queueflow.repository.PrescriptionItemRepository;

@Service
public class PatientPrescriptionDashboardService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;

    public PatientPrescriptionDashboardService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientPrescriptionDashboardResponse> getPatientPrescriptions(
            Long patientId) {

        List<Prescription> prescriptions =
                prescriptionRepository
                        .findAllByConsultation_QueueTicket_Patient_IdOrderByPrescribedAtDesc(
                                patientId);

        List<PatientPrescriptionDashboardResponse> responses =
                new ArrayList<>();

        for (Prescription prescription : prescriptions) {

            DoctorProfile doctor =
                    prescription.getConsultation().getDoctor();

            AppUser user = doctor.getClinicStaff().getUser();

            List<PrescriptionItem> items =
                    prescriptionItemRepository
                            .findAllByPrescription_Id(prescription.getId());

            List<PatientPrescriptionDashboardResponse.MedicineResponse>
                    medicines = new ArrayList<>();

            for (PrescriptionItem item : items) {

                PatientPrescriptionDashboardResponse.MedicineResponse medicine =
                        new PatientPrescriptionDashboardResponse.MedicineResponse(
                                item.getId(),
                                item.getMedicineName(),
                                item.getDosage(),
                                item.getFrequency(),
                                item.getDurationDays(),
                                item.getQuantity(),
                                item.getInstructions()
                        );

                medicines.add(medicine);
            }

            PatientPrescriptionDashboardResponse response =
                    new PatientPrescriptionDashboardResponse(
                            prescription.getId(),
                            prescription.getConsultation().getId(),
                            user.getFullName(),
                            prescription.getPrescribedAt(),
                            prescription.getInstructions(),
                            medicines
                    );

            responses.add(response);
        }

        return responses;
    }
}
