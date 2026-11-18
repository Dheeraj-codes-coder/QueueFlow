
package com.queueflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.Clinic;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    boolean existsByEmail(String email);

}
