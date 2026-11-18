
package com.queueflow.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.AppointmentSlotGenerationResponse;
import com.queueflow.entity.AppointmentSlot;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.entity.DoctorSchedule;
import com.queueflow.enums.SlotStatus;
import com.queueflow.repository.AppointmentSlotRepository;
import com.queueflow.repository.DoctorProfileRepository;
import com.queueflow.repository.DoctorScheduleRepository;

@Service
public class AppointmentSlotGenerationService {

    private final AppointmentSlotRepository appointmentSlotRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public AppointmentSlotGenerationService(
            AppointmentSlotRepository appointmentSlotRepository,
            DoctorProfileRepository doctorProfileRepository,
            DoctorScheduleRepository doctorScheduleRepository) {

        this.appointmentSlotRepository = appointmentSlotRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    @Transactional
    public AppointmentSlotGenerationResponse generateSlots(Long doctorId) {

        DoctorProfile doctor = doctorProfileRepository.findByIdForUpdate(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!doctor.isActive()) {
            throw new RuntimeException("Doctor is not active");
        }

        int duration = doctor.getConsultationDurationMinutes();

        if (duration <= 0) {
            throw new RuntimeException("Invalid consultation duration");
        }

        List<DoctorSchedule> schedules =
                doctorScheduleRepository
                        .findAllByDoctor_IdOrderByDayOfWeekAscStartTimeAsc(doctorId);

        if (schedules.isEmpty()) {
            throw new RuntimeException("Doctor has no weekly schedule");
        }

        int totalSlotsGenerated = 0;

        LocalDate today = LocalDate.now();

        for (int i = 0; i < 30; i++) {

            LocalDate slotDate = today.plusDays(i);

            for (DoctorSchedule schedule : schedules) {

                if (!schedule.isAvailable()) {
                    continue;
                }

                if (!schedule.getDayOfWeek().equals(slotDate.getDayOfWeek())) {
                    continue;
                }

                LocalTime startTime = schedule.getStartTime();
                LocalTime scheduleEndTime = schedule.getEndTime();

                while (!startTime.plusMinutes(duration).isAfter(scheduleEndTime)) {

                    LocalTime endTime = startTime.plusMinutes(duration);

                    boolean exists = appointmentSlotRepository
                            .existsByDoctor_IdAndSlotDateAndStartTime(
                                    doctorId,
                                    slotDate,
                                    startTime
                            );

                    if (!exists) {

                        AppointmentSlot slot = new AppointmentSlot();

                        slot.setDoctor(doctor);
                        slot.setSlotDate(slotDate);
                        slot.setStartTime(startTime);
                        slot.setEndTime(endTime);
                        slot.setStatus(SlotStatus.AVAILABLE);

                        appointmentSlotRepository.save(slot);

                        totalSlotsGenerated++;
                    }

                    startTime = endTime;
                }
            }
        }

        return new AppointmentSlotGenerationResponse(
                doctorId,
                totalSlotsGenerated,
                "Appointment slots generated successfully"
        );
    }

    public List<AppointmentSlot> getDoctorSlots(Long doctorId) {

        doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return appointmentSlotRepository
                .findAllByDoctor_IdOrderBySlotDateAscStartTimeAsc(doctorId);
    }
}
