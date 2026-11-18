
package com.queueflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.DoctorScheduleRequest;
import com.queueflow.dto.DoctorScheduleUpdateRequest;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.entity.DoctorSchedule;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.repository.DoctorScheduleRepository;

@Service
public class DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorScheduleService(
            DoctorScheduleRepository doctorScheduleRepository,
            DoctorProfileRepository doctorProfileRepository) {

        this.doctorScheduleRepository = doctorScheduleRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Transactional
    public DoctorSchedule createSchedule(Long doctorId, DoctorScheduleRequest request) {

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!doctor.isActive()) {
            throw new RuntimeException("Doctor is not active");
        }

        boolean overlap = doctorScheduleRepository
                .existsByDoctor_IdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                        doctorId,
                        request.getDayOfWeek(),
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (overlap) {
            throw new RuntimeException("Doctor already has a schedule during this time");
        }

        DoctorSchedule schedule = new DoctorSchedule();

        schedule.setDoctor(doctor);
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setAvailable(request.isAvailable());

        return doctorScheduleRepository.save(schedule);
    }

    public List<DoctorSchedule> getDoctorSchedule(Long doctorId) {

        doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return doctorScheduleRepository
                .findAllByDoctor_IdOrderByDayOfWeekAscStartTimeAsc(doctorId);
    }

    @Transactional
    public DoctorSchedule updateSchedule(
            Long scheduleId,
            DoctorScheduleUpdateRequest request) {

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        Long doctorId = schedule.getDoctor().getId();

        boolean overlap = doctorScheduleRepository
                .existsByDoctor_IdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                        doctorId,
                        schedule.getDayOfWeek(),
                        request.getEndTime(),
                        request.getStartTime(),
                        scheduleId
                );

        if (overlap) {
            throw new RuntimeException("Doctor already has a schedule during this time");
        }

        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setAvailable(request.isAvailable());

        return doctorScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {

        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        doctorScheduleRepository.delete(schedule);
    }
}
