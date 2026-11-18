package com.queueflow.dto;

public class PatientDashboardResponse {

    private Long patientId;
    private String patientName;
    private String patientEmail;
    private int totalAppointments;
    private int upcomingAppointments;
    private int completedAppointments;
    private int cancelledAppointments;

    public PatientDashboardResponse(
            Long patientId,
            String patientName,
            String patientEmail,
            int totalAppointments,
            int upcomingAppointments,
            int completedAppointments,
            int cancelledAppointments) {

        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.totalAppointments = totalAppointments;
        this.upcomingAppointments = upcomingAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }
}