
package com.queueflow.dto;

public class DoctorDashboardStatisticsResponse {

    private Long doctorId;
    private long totalAppointments;
    private long bookedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long waitingPatients;
    private long patientsInConsultation;

    public DoctorDashboardStatisticsResponse(
            Long doctorId,
            long totalAppointments,
            long bookedAppointments,
            long completedAppointments,
            long cancelledAppointments,
            long waitingPatients,
            long patientsInConsultation) {

        this.doctorId = doctorId;
        this.totalAppointments = totalAppointments;
        this.bookedAppointments = bookedAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.waitingPatients = waitingPatients;
        this.patientsInConsultation = patientsInConsultation;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public long getBookedAppointments() {
        return bookedAppointments;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public long getWaitingPatients() {
        return waitingPatients;
    }

    public long getPatientsInConsultation() {
        return patientsInConsultation;
    }
}
