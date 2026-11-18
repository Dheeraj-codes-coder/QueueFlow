package com.queueflow.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.queueflow.entity.Appointment;
import com.queueflow.enums.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPatient_IdOrderByBookedAtDesc(Long patientId);

    List<Appointment> findAllByDoctor_IdAndSlot_SlotDateOrderBySlot_StartTimeAsc(
            Long doctorId,
            LocalDate appointmentDate);

    Optional<Appointment> findByIdAndPatient_Id(
            Long appointmentId,
            Long patientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.id = :appointmentId AND a.patient.id = :patientId")
    Optional<Appointment> findByIdAndPatientIdForUpdate(
            @Param("appointmentId") Long appointmentId,
            @Param("patientId") Long patientId);

    List<Appointment> findAllByDoctor_IdAndSlot_SlotDateGreaterThanEqualAndStatusInOrderBySlot_SlotDateAscSlot_StartTimeAsc(
            Long doctorId,
            LocalDate date,
            List<AppointmentStatus> statuses);

    List<Appointment> findAllByPatient_IdAndSlot_SlotDateGreaterThanEqualAndStatusInOrderBySlot_SlotDateAscSlot_StartTimeAsc(
            Long patientId,
            LocalDate date,
            List<AppointmentStatus> statuses);

    boolean existsByIdAndDoctor_ClinicStaff_User_Email(
            Long appointmentId,
            String email);
}