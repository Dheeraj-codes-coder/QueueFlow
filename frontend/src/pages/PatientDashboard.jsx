
import { useEffect, useState } from 'react'
import api from '../services/api'

function PatientDashboard() {

  const user = JSON.parse(localStorage.getItem('user'))

  const [patient, setPatient] = useState(null)
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  const getPatientDashboard = async () => {
    try {
      const response = await api.get('/api/patients/me/dashboard', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })

      setPatient(response.data)
      setMessage('')
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Unable to load patient dashboard'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    getPatientDashboard()
  }, [])

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/'
  }

  return (
    <div className="container mt-4">
      <div className="card shadow p-4">

        <h2>QueueFlow</h2>
        <h4 className="mt-3">Patient Dashboard</h4>

        <p>Welcome, {user?.fullName}</p>
        <p>Manage your appointments and medical activities.</p>

        {loading && <p>Loading patient details...</p>}

        {message && (
          <div className="alert alert-danger">{message}</div>
        )}

        {patient && (
          <div className="row mt-3">

            <div className="col-md-3 mb-3">
              <div className="card p-3">
                <h6>Total Appointments</h6>
                <h3>{patient.totalAppointments}</h3>
              </div>
            </div>

            <div className="col-md-3 mb-3">
              <div className="card p-3">
                <h6>Upcoming Appointments</h6>
                <h3>{patient.upcomingAppointments}</h3>
              </div>
            </div>

            <div className="col-md-3 mb-3">
              <div className="card p-3">
                <h6>Completed Appointments</h6>
                <h3>{patient.completedAppointments}</h3>
              </div>
            </div>

            <div className="col-md-3 mb-3">
              <div className="card p-3">
                <h6>Cancelled Appointments</h6>
                <h3>{patient.cancelledAppointments}</h3>
              </div>
            </div>

          </div>
        )}

        <div className="row mt-4">

          <div className="col-md-4 mb-3">
            <div
              className="card p-3 h-100"
              style={{ cursor: 'pointer' }}
              onClick={() => window.location.href = '/book-appointment'}
            >
              <h5>Book Appointment</h5>
              <p>Find a doctor and book an appointment.</p>
              <button className="btn btn-primary">
                Book Now
              </button>
            </div>
          </div>

          <div className="col-md-4 mb-3">
            <div
              className="card p-3 h-100"
              style={{ cursor: 'pointer' }}
              onClick={() => window.location.href = '/patient-appointments'}
            >
              <h5>My Appointments</h5>
              <p>View your appointments.</p>
              <button className="btn btn-primary">
                View Appointments
              </button>
            </div>
          </div>

          <div className="col-md-4 mb-3">
            <div className="card p-3 h-100">
              <h5>Prescriptions</h5>
              <p>View your prescriptions.</p>
            </div>
          </div>

        </div>

        <button className="btn btn-danger mt-3" onClick={logout}>
          Logout
        </button>

      </div>
    </div>
  )
}

export default PatientDashboard
