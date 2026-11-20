
import { useEffect, useState } from 'react'
import api from '../services/api'

function PatientAppointments() {

  const [appointments, setAppointments] = useState([])
  const [patientId, setPatientId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')
  const [checkedInAppointments, setCheckedInAppointments] = useState([])

  const getAppointments = async () => {
    try {
      const token = localStorage.getItem('token')

      const patientResponse = await api.get(
        '/api/patients/me/dashboard',
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      const id = patientResponse.data.patientId
      setPatientId(id)

      const response = await api.get(
        `/api/patients/${id}/appointments`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
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
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
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
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
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

  return (
    <div className="container mt-4">
      <div className="card shadow p-4">
        <h2>QueueFlow</h2>
        <h4 className="mt-3">My Appointments</h4>
        <p>View and manage your appointments.</p>

        {message && <div className="alert alert-danger">{message}</div>}
        {success && <div className="alert alert-success">{success}</div>}
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
