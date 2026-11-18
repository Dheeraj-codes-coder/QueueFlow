
package com.queueflow.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.entity.AppointmentSlot;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.enums.SlotStatus;
import com.queueflow.repository.AppointmentSlotRepository;
import com.queueflow.repository.DoctorProfileRepository;

@Service
public class AppointmentSlotService {

    private final AppointmentSlotRepository appointmentSlotRepository;

    private final DoctorProfileRepository doctorProfileRepository;

    public AppointmentSlotService(
            AppointmentSlotRepository appointmentSlotRepository,
            DoctorProfileRepository doctorProfileRepository) {

        this.appointmentSlotRepository = appointmentSlotRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Transactional
    public AppointmentSlot createSlot(
            Long doctorId,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime) {

        // Check time values

        if (startTime == null || endTime == null) {
            throw new RuntimeException("Start time and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }

        if (slotDate == null || slotDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Invalid slot date");
        }

        // Lock and find doctor

        DoctorProfile doctor = doctorProfileRepository.findByIdForUpdate(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check overlapping slots

        boolean exists = appointmentSlotRepository
                .existsByDoctor_IdAndSlotDateAndStartTimeLessThanAndEndTimeGreaterThan(
                        doctorId,
                        slotDate,
                        endTime,
                        startTime
                );

        if (exists) {
            throw new RuntimeException("This time slot overlaps an existing slot");
        }

        // Create new slot

        AppointmentSlot slot = new AppointmentSlot();

        slot.setDoctor(doctor);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(SlotStatus.AVAILABLE);

        return appointmentSlotRepository.save(slot);
    }
}
