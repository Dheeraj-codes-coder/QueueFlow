
package com.queueflow.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.queueflow.entity.QueueTicket;
import com.queueflow.enums.QueueStatus;

import jakarta.persistence.LockModeType;

public interface QueueTicketRepository extends JpaRepository<QueueTicket, Long> {

    boolean existsByAppointment_Id(Long appointmentId);

    boolean existsByDoctor_IdAndQueueDateAndStatusIn(
            Long doctorId,
            LocalDate queueDate,
            List<QueueStatus> statuses);

    @Query("SELECT COALESCE(MAX(q.tokenNumber), 0) FROM QueueTicket q WHERE q.doctor.id = :doctorId AND q.queueDate = :queueDate")
    Integer findMaximumTokenNumber(
            @Param("doctorId") Long doctorId,
            @Param("queueDate") LocalDate queueDate);

    List<QueueTicket> findAllByDoctor_IdAndQueueDateOrderByTokenNumberAsc(
            Long doctorId,
            LocalDate queueDate);

    Optional<QueueTicket> findFirstByDoctor_IdAndQueueDateAndStatusOrderByTokenNumberAsc(
            Long doctorId,
            LocalDate queueDate,
            QueueStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QueueTicket q WHERE q.id = :ticketId")
    Optional<QueueTicket> findByIdForUpdate(
            @Param("ticketId") Long ticketId);
}
