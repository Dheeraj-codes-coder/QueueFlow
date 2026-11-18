
package com.queueflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.PrescriptionItem;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    List<PrescriptionItem> findAllByPrescription_Id(Long prescriptionId);

}
