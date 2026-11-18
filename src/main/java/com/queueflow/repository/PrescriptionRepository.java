package com.queueflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByConsultation_Id(Long consultationId);

    List<Prescription> findAllByConsultation_QueueTicket_Patient_IdOrderByPrescribedAtDesc(
            Long patientId
    );

    boolean existsByIdAndConsultation_Doctor_ClinicStaff_User_Email(
            Long prescriptionId,
            String email
    );

    boolean existsByIdAndConsultation_QueueTicket_Patient_User_Email(
            Long prescriptionId,
            String email
    );
}