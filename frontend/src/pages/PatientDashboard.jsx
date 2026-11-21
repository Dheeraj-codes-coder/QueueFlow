
import { useEffect, useState } from 'react'
import api from '../services/api'
import './Dashboard.css'

function PatientDashboard() {

  const user = JSON.parse(localStorage.getItem('user'))

  const [patient, setPatient] = useState(null)
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)

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

  const openPage = (path) => {
    setMenuOpen(false)
    window.location.href = path
  }

  return (
    <div className="dashboard-page">

      {menuOpen && (
        <button
          className="dashboard-overlay"
          aria-label="Close navigation menu"
          onClick={() => setMenuOpen(false)}
        />
      )}

      <aside className={`dashboard-sidebar ${menuOpen ? 'mobile-open' : ''}`}>

        <div className="dashboard-brand">
          <div className="dashboard-brand-icon">+</div>
          <span>QueueFlow</span>

          <button
            className="sidebar-close"
            onClick={() => setMenuOpen(false)}
            aria-label="Close menu"
          >
            ×
          </button>
        </div>

        <p className="sidebar-label">PATIENT MENU</p>

        <button
          className="sidebar-link active"
          onClick={() => openPage('/patient-dashboard')}
        >
          <span>▦</span> Dashboard
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/book-appointment')}
        >
          <span>＋</span> Book Appointment
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/patient-appointments')}
        >
          <span>▣</span> My Appointments
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/patient-prescriptions')}
        >
          <span>▤</span> Prescriptions
        </button>

        <div className="sidebar-bottom">
          <button className="sidebar-logout" onClick={logout}>
            ↪ Logout
          </button>
        </div>

      </aside>

      <main className="dashboard-main">

        <header className="dashboard-header">

          <div className="dashboard-header-left">

            <button
              className="mobile-menu-button"
              onClick={() => setMenuOpen(true)}
              aria-label="Open navigation menu"
            >
              ☰
            </button>

            <div>
              <h2>Patient Dashboard</h2>
              <p>Manage your healthcare in one place.</p>
            </div>

          </div>

          <div className="dashboard-user">
            <div className="user-avatar">
              {user?.fullName?.charAt(0)?.toUpperCase() || 'P'}
            </div>
            <div>
              <strong>{user?.fullName}</strong>
              <span>Patient</span>
            </div>
          </div>

        </header>

        <section className="dashboard-welcome">
          <div>
            <span>YOUR HEALTHCARE OVERVIEW</span>
            <h1>Hello, {user?.fullName || 'Patient'}!</h1>
            <p>
              Keep track of your appointments and medical records.
            </p>
          </div>
          <div className="welcome-symbol">✚</div>
        </section>

        {loading && (
          <p className="dashboard-loading">Loading patient details...</p>
        )}

        {message && (
          <div className="dashboard-error">{message}</div>
        )}

        {patient && (
          <section className="dashboard-section">

            <div className="section-heading">
              <h3>Appointment Overview</h3>
              <p>Your appointment statistics</p>
            </div>

            <div className="dashboard-stats">

              <div className="stat-card">
                <div className="stat-icon blue">▦</div>
                <p>Total Appointments</p>
                <h2>{patient.totalAppointments}</h2>
              </div>

              <div className="stat-card">
                <div className="stat-icon green">◷</div>
                <p>Upcoming Appointments</p>
                <h2>{patient.upcomingAppointments}</h2>
              </div>

              <div className="stat-card">
                <div className="stat-icon purple">✓</div>
                <p>Completed Appointments</p>
                <h2>{patient.completedAppointments}</h2>
              </div>

              <div className="stat-card">
                <div className="stat-icon orange">×</div>
                <p>Cancelled Appointments</p>
                <h2>{patient.cancelledAppointments}</h2>
              </div>

            </div>
          </section>
        )}

        <section className="dashboard-section">

          <div className="section-heading">
            <h3>Quick Actions</h3>
            <p>Access your healthcare services</p>
          </div>

          <div className="dashboard-actions">

            <div className="action-card">
              <div className="action-icon blue">＋</div>
              <h4>Book Appointment</h4>
              <p>Find a doctor and book your next appointment.</p>
              <button onClick={() => openPage('/book-appointment')}>
                Book Now <span>→</span>
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon green">▣</div>
              <h4>My Appointments</h4>
              <p>View, reschedule and manage your appointments.</p>
              <button onClick={() => openPage('/patient-appointments')}>
                View Appointments <span>→</span>
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon purple">▤</div>
              <h4>Prescriptions</h4>
              <p>View your prescribed medicines and download PDFs.</p>
              <button onClick={() => openPage('/patient-prescriptions')}>
                View Prescriptions <span>→</span>
              </button>
            </div>

          </div>
        </section>

        <footer className="dashboard-footer">
          QueueFlow · Healthcare management made simpler.
        </footer>

      </main>

    </div>
  )
}

export default PatientDashboard
