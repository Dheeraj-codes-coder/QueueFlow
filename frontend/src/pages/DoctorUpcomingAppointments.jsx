
import { useEffect, useState } from 'react'
import api from '../services/api'

function DoctorUpcomingAppointments() {

  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  useEffect(() => {
    getAppointments()
  }, [])

  const getAppointments = async () => {
    try {
      const today = new Date()
      const date =
        today.getFullYear() + '-' +
        String(today.getMonth() + 1).padStart(2, '0') + '-' +
        String(today.getDate()).padStart(2, '0')

      const response = await api.get(
        '/api/doctors/1/appointments/upcoming',
        {
          params: {
            date: date
          },
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setAppointments(response.data)

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to load upcoming appointments'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container mt-4">

      <h2>Upcoming Appointments</h2>
      <p>View your upcoming patient appointments.</p>

      {loading && <p>Loading appointments...</p>}

      {message && (
        <div className="alert alert-danger">
          {message}
        </div>
      )}

      {!loading && !message && appointments.length === 0 && (
        <div className="alert alert-info">
          No upcoming appointments found.
        </div>
      )}

      {appointments.length > 0 && (
        <div className="table-responsive">

          <table className="table table-bordered table-striped">

            <thead>
              <tr>
                <th>Patient Name</th>
                <th>Date</th>
                <th>Start Time</th>
                <th>End Time</th>
                <th>Status</th>
                <th>Reason</th>
              </tr>
            </thead>

            <tbody>
              {appointments.map((appointment) => (
                <tr key={appointment.appointmentId}>
                  <td>{appointment.patientName}</td>
                  <td>{appointment.appointmentDate}</td>
                  <td>{appointment.startTime}</td>
                  <td>{appointment.endTime}</td>
                  <td>{appointment.status}</td>
                  <td>{appointment.reason}</td>
                </tr>
              ))}
            </tbody>

          </table>

        </div>
      )}

      <button
        className="btn btn-secondary mt-3"
        onClick={() => window.location.href = '/doctor-dashboard'}
      >
        Back to Dashboard
      </button>

    </div>
  )
}

export default DoctorUpcomingAppointments
