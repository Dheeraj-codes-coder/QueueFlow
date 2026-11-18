package com.queueflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.Consultation;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    Optional<Consultation> findByQueueTicket_Id(Long queueTicketId);

    List<Consultation> findAllByDoctor_IdOrderByStartedAtDesc(Long doctorId);
}