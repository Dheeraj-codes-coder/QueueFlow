
import { useCallback, useEffect, useState } from 'react'
import api from '../services/api'

function DoctorAppointments() {

  const [appointments, setAppointments] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')

  const getAppointments = useCallback(async () => {
    try {
      setLoading(true)
      setMessage('')

      const user = JSON.parse(localStorage.getItem('user'))
      const token = localStorage.getItem('token')

      const doctorsResponse = await api.get('/api/doctors')

      const doctor = doctorsResponse.data.find(
        item =>
          item.fullName?.trim().toLowerCase() ===
          user?.fullName?.trim().toLowerCase()
      )

      if (!doctor) {
        setAppointments([])
        setMessage('Doctor profile not found for this account.')
        return
      }

      const today = new Date()
      const date =
        today.getFullYear() + '-' +
        String(today.getMonth() + 1).padStart(2, '0') + '-' +
        String(today.getDate()).padStart(2, '0')

      const response = await api.get(
        `/api/doctors/${doctor.doctorId}/appointments/daily`,
        {
          params: { date: date },
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      setAppointments(response.data)

    } catch (error) {
      setAppointments([])
      setMessage(
        error.response?.data?.message ||
        'Unable to load appointments.'
      )
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    getAppointments()
  }, [getAppointments])

  const confirmedCount = appointments.filter(
    item => item.status === 'CONFIRMED' || item.status === 'BOOKED'
  ).length

  const completedCount = appointments.filter(
    item => item.status === 'COMPLETED'
  ).length

  const cancelledCount = appointments.filter(
    item => item.status === 'CANCELLED'
  ).length

  const getStatusStyle = (status) => {
    if (status === 'CONFIRMED' || status === 'BOOKED') {
      return { background: '#dbeafe', color: '#1d4ed8' }
    }

    if (status === 'COMPLETED') {
      return { background: '#dcfce7', color: '#15803d' }
    }

    if (status === 'CANCELLED') {
      return { background: '#fee2e2', color: '#b91c1c' }
    }

    if (status === 'PENDING') {
      return { background: '#fff4d6', color: '#9a6700' }
    }

    return { background: '#f1f5f9', color: '#475569' }
  }

  const cardStyle = {
    background: '#ffffff',
    border: '1px solid #e8edf3',
    borderRadius: '16px',
    padding: '22px',
    boxShadow: '0 4px 15px rgba(15, 23, 42, 0.04)'
  }

  return (
    <div style={{
      minHeight: '100vh',
      background: '#f5f7fb',
      padding: '30px 15px'
    }}>

      <div style={{ maxWidth: '1200px', margin: 'auto' }}>

        {/* Header */}

        <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">

          <div>
            <p style={{
              color: '#64748b',
              fontSize: '13px',
              fontWeight: '600',
              marginBottom: '6px'
            }}>
              DOCTOR WORKSPACE / APPOINTMENTS
            </p>

            <h2 style={{
              color: '#172554',
              fontWeight: '750',
              marginBottom: '5px'
            }}>
              Today's Appointments
            </h2>

            <p style={{ color: '#64748b', margin: 0 }}>
              View and track your scheduled patient appointments.
            </p>
          </div>

          <button
            className="btn"
            style={{
              background: '#ffffff',
              border: '1px solid #dbe3ee',
              color: '#334155',
              borderRadius: '9px',
              padding: '10px 16px',
              fontWeight: '600'
            }}
            onClick={getAppointments}
            disabled={loading}
          >
            ↻ Refresh
          </button>

        </div>

        {/* Error */}

        {message && (
          <div className="alert alert-danger">
            {message}
          </div>
        )}

        {/* Summary Cards */}

        <div className="row g-3 mb-4">

          <div className="col-12 col-md-4">
            <div style={cardStyle}>
              <p style={{ color: '#64748b', marginBottom: '10px' }}>
                Total Appointments
              </p>

              <h2 style={{
                color: '#2563eb',
                fontWeight: '750',
                margin: 0
              }}>
                {loading ? '—' : appointments.length}
              </h2>

              <small style={{ color: '#94a3b8' }}>
                Scheduled for today
              </small>
            </div>
          </div>

          <div className="col-12 col-md-4">
            <div style={cardStyle}>
              <p style={{ color: '#64748b', marginBottom: '10px' }}>
                Confirmed / Booked
              </p>

              <h2 style={{
                color: '#0d9488',
                fontWeight: '750',
                margin: 0
              }}>
                {loading ? '—' : confirmedCount}
              </h2>

              <small style={{ color: '#94a3b8' }}>
                Active appointments
              </small>
            </div>
          </div>

          <div className="col-12 col-md-4">
            <div style={cardStyle}>
              <p style={{ color: '#64748b', marginBottom: '10px' }}>
                Completed
              </p>

              <h2 style={{
                color: '#16a34a',
                fontWeight: '750',
                margin: 0
              }}>
                {loading ? '—' : completedCount}
              </h2>

              <small style={{ color: '#94a3b8' }}>
                Finished consultations
              </small>
            </div>
          </div>

        </div>

        {/* Appointment Table */}

        <div style={cardStyle}>

          <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">

            <div>
              <h5 style={{
                color: '#172554',
                fontWeight: '700',
                marginBottom: '5px'
              }}>
                Patient Appointment List
              </h5>

              <p style={{
                color: '#94a3b8',
                fontSize: '13px',
                margin: 0
              }}>
                Today's appointment schedule and patient details.
              </p>
            </div>

            <span style={{
              background: '#eff6ff',
              color: '#2563eb',
              padding: '7px 12px',
              borderRadius: '20px',
              fontSize: '13px',
              fontWeight: '600'
            }}>
              {loading ? 'Loading...' : `${appointments.length} Appointments`}
            </span>

          </div>

          {loading && (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
              <p className="mt-3 text-muted">Loading appointments...</p>
            </div>
          )}

          {!loading && !message && appointments.length === 0 && (
            <div className="text-center py-5">

              <div style={{
                fontSize: '42px',
                marginBottom: '12px'
              }}>
                📅
              </div>

              <h5 style={{ color: '#334155', fontWeight: '650' }}>
                No appointments today
              </h5>

              <p style={{ color: '#94a3b8', margin: 0 }}>
                Your scheduled patient appointments will appear here.
              </p>

            </div>
          )}

          {!loading && !message && appointments.length > 0 && (

            <div className="table-responsive">

              <table className="table align-middle mb-0">

                <thead>
                  <tr style={{
                    background: '#f8fafc',
                    color: '#64748b',
                    fontSize: '12px',
                    textTransform: 'uppercase'
                  }}>
                    <th className="py-3">Patient</th>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Status</th>
                    <th>Reason</th>
                  </tr>
                </thead>

                <tbody>

                  {appointments.map(appointment => (

                    <tr key={appointment.appointmentId}>

                      <td style={{
                        color: '#1e293b',
                        fontWeight: '600'
                      }}>
                        {appointment.patientName}
                      </td>

                      <td style={{ color: '#64748b' }}>
                        {appointment.appointmentDate}
                      </td>

                      <td style={{ color: '#64748b' }}>
                        {appointment.startTime}
                      </td>

                      <td style={{ color: '#64748b' }}>
                        {appointment.endTime}
                      </td>

                      <td>
                        <span style={{
                          ...getStatusStyle(appointment.status),
                          padding: '6px 11px',
                          borderRadius: '20px',
                          fontSize: '12px',
                          fontWeight: '700',
                          whiteSpace: 'nowrap'
                        }}>
                          {appointment.status?.replaceAll('_', ' ')}
                        </span>
                      </td>

                      <td style={{
                        color: '#64748b',
                        maxWidth: '220px'
                      }}>
                        {appointment.reason || '—'}
                      </td>

                    </tr>

                  ))}

                </tbody>

              </table>

            </div>
          )}

        </div>

        {/* Footer */}

        <button
          className="btn mt-4"
          style={{
            background: '#e2e8f0',
            color: '#334155',
            borderRadius: '9px',
            padding: '10px 18px',
            fontWeight: '600'
          }}
          onClick={() => window.location.href = '/doctor-dashboard'}
        >
          ← Back to Dashboard
        </button>

        <p className="text-center mt-4" style={{
          color: '#94a3b8',
          fontSize: '12px'
        }}>
          QueueFlow · Healthcare management made simpler
        </p>

      </div>
    </div>
  )
}

export default DoctorAppointments
