
import { useEffect, useState } from 'react'
import api from '../services/api'

function PatientAppointments() {

  const [appointments, setAppointments] = useState([])
  const [patientId, setPatientId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')
  const [checkedInAppointments, setCheckedInAppointments] = useState([])

  const [reschedulingAppointment, setReschedulingAppointment] = useState(null)
  const [availableSlots, setAvailableSlots] = useState([])
  const [selectedSlotId, setSelectedSlotId] = useState('')

  const token = localStorage.getItem('token')

  const headers = {
    Authorization: `Bearer ${token}`
  }

  const getAppointments = async () => {
    try {
      const patientResponse = await api.get(
        '/api/patients/me/dashboard',
        { headers }
      )

      const id = patientResponse.data.patientId
      setPatientId(id)

      const response = await api.get(
        `/api/patients/${id}/appointments`,
        { headers }
      )

      setAppointments(response.data)
      setMessage('')
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Unable to load appointments'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    getAppointments()
  }, [])

  const cancelAppointment = async (appointmentId) => {
    const confirmCancel = window.confirm(
      'Are you sure you want to cancel this appointment?'
    )

    if (!confirmCancel) {
      return
    }

    try {
      setMessage('')
      setSuccess('')

      const response = await api.delete(
        `/api/appointments/${appointmentId}/cancel?patientId=${patientId}`,
        { headers }
      )

      setSuccess(response.data.message)
      await getAppointments()
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Unable to cancel appointment'
      )
    }
  }

  const checkInAppointment = async (appointmentId) => {
    try {
      setMessage('')
      setSuccess('')

      const response = await api.post(
        '/api/queue/patient/check-in',
        {
          appointmentId: appointmentId
        },
        { headers }
      )

      setSuccess(
        response.data.message +
        '. Your token number is ' +
        response.data.tokenNumber
      )

      setCheckedInAppointments([
        ...checkedInAppointments,
        appointmentId
      ])

    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Unable to check in'
      )
    }
  }

  const openReschedule = async (appointment) => {
    try {
      setMessage('')
      setSuccess('')
      setSelectedSlotId('')
      setReschedulingAppointment(appointment)

      const response = await api.get(
        `/api/doctors/${appointment.doctorId}/slots`,
        { headers }
      )

      const today = new Date()
      const todayString =
        today.getFullYear() + '-' +
        String(today.getMonth() + 1).padStart(2, '0') + '-' +
        String(today.getDate()).padStart(2, '0')

      const slots = response.data.filter(
        slot =>
          slot.status === 'AVAILABLE' &&
          slot.slotDate >= todayString
      )

      setAvailableSlots(slots)

    } catch (error) {
      setReschedulingAppointment(null)
      setMessage(
        error.response?.data?.message ||
        'Unable to load available slots'
      )
    }
  }

  const rescheduleAppointment = async (e) => {
    e.preventDefault()

    try {
      setMessage('')
      setSuccess('')

      const response = await api.put(
        `/api/appointments/${reschedulingAppointment.appointmentId}/reschedule`,
        {
          patientId: patientId,
          newSlotId: Number(selectedSlotId)
        },
        { headers }
      )

      setSuccess(response.data.message)

      setReschedulingAppointment(null)
      setAvailableSlots([])
      setSelectedSlotId('')

      await getAppointments()

    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Unable to reschedule appointment'
      )
    }
  }

  return (
    <div className="container mt-4">
      <div className="card shadow p-4">

        <h2>QueueFlow</h2>
        <h4 className="mt-3">My Appointments</h4>
        <p>View and manage your appointments.</p>

        {message && (
          <div className="alert alert-danger">{message}</div>
        )}

        {success && (
          <div className="alert alert-success">{success}</div>
        )}

        {loading && <p>Loading appointments...</p>}

        {!loading && !message && appointments.length === 0 && (
          <div className="alert alert-info">
            You have no appointments.
          </div>
        )}

        {!loading && appointments.length > 0 && (
          <div className="table-responsive">
            <table className="table table-bordered table-striped">
              <thead>
                <tr>
                  <th>Appointment ID</th>
                  <th>Doctor Name</th>
                  <th>Clinic</th>
                  <th>Date</th>
                  <th>Start Time</th>
                  <th>End Time</th>
                  <th>Status</th>
                  <th>Reason</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {appointments.map((appointment) => (
                  <tr key={appointment.appointmentId}>
                    <td>{appointment.appointmentId}</td>
                    <td>{appointment.doctorName}</td>
                    <td>{appointment.clinicName}</td>
                    <td>{appointment.appointmentDate}</td>
                    <td>{appointment.startTime}</td>
                    <td>{appointment.endTime}</td>
                    <td>{appointment.status}</td>
                    <td>{appointment.reason}</td>

                    <td>
                      {(appointment.status === 'BOOKED' ||
                        appointment.status === 'CONFIRMED') && (
                        <>
                          {checkedInAppointments.includes(
                            appointment.appointmentId
                          ) ? (
                            <span className="badge bg-success">
                              Checked In
                            </span>
                          ) : (
                            <button
                              className="btn btn-primary btn-sm mb-2"
                              onClick={() =>
                                checkInAppointment(
                                  appointment.appointmentId
                                )
                              }
                            >
                              Check In
                            </button>
                          )}

                          <br />

                          <button
                            className="btn btn-warning btn-sm mb-2"
                            onClick={() => openReschedule(appointment)}
                          >
                            Reschedule
                          </button>

                          <br />

                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() =>
                              cancelAppointment(
                                appointment.appointmentId
                              )
                            }
                          >
                            Cancel
                          </button>
                        </>
                      )}

                      {appointment.status !== 'BOOKED' &&
                        appointment.status !== 'CONFIRMED' && (
                          <span>-</span>
                        )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {reschedulingAppointment && (
          <div className="card border-warning p-3 mt-4">

            <h5>Reschedule Appointment</h5>

            <p>
              Doctor: {reschedulingAppointment.doctorName}
            </p>

            <p>
              Current Date: {reschedulingAppointment.appointmentDate}
            </p>

            <form onSubmit={rescheduleAppointment}>

              <div className="mb-3">
                <label className="form-label">
                  Select New Appointment Slot
                </label>

                <select
                  className="form-select"
                  value={selectedSlotId}
                  onChange={(e) => setSelectedSlotId(e.target.value)}
                  required
                >
                  <option value="">Select a new time slot</option>

                  {availableSlots.map(slot => (
                    <option
                      key={slot.slotId}
                      value={slot.slotId}
                    >
                      {slot.slotDate} | {slot.startTime} - {slot.endTime}
                    </option>
                  ))}
                </select>
              </div>

              {availableSlots.length === 0 && (
                <div className="alert alert-info">
                  No upcoming available slots found for this doctor.
                </div>
              )}

              <button
                type="submit"
                className="btn btn-warning me-2"
                disabled={!selectedSlotId}
              >
                Confirm Reschedule
              </button>

              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => {
                  setReschedulingAppointment(null)
                  setAvailableSlots([])
                  setSelectedSlotId('')
                }}
              >
                Close
              </button>

            </form>
          </div>
        )}

        <button
          className="btn btn-secondary mt-3"
          onClick={() => window.location.href = '/patient-dashboard'}
        >
          Back to Dashboard
        </button>

      </div>
    </div>
  )
}

export default PatientAppointments
