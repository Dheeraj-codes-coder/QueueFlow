
package com.queueflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.PatientProfile;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    boolean existsByIdAndUser_Email(Long patientId, String email);

}
