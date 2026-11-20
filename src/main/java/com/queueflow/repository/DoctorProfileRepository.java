
package com.queueflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.queueflow.entity.DoctorProfile;
import com.queueflow.enums.DoctorSpecialty;

import jakarta.persistence.LockModeType;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DoctorProfile d WHERE d.id = :doctorId")
    Optional<DoctorProfile> findByIdForUpdate(@Param("doctorId") Long doctorId);

    boolean existsByIdAndClinicStaff_User_Email(Long doctorId, String email);

    List<DoctorProfile> findByActiveTrueOrderBySpecialtyAscExperienceYearsDesc();

    List<DoctorProfile> findByActiveTrueAndSpecialtyOrderByExperienceYearsDesc(
            DoctorSpecialty specialty
    );
}
