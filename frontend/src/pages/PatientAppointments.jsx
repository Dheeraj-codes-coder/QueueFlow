import { useEffect, useMemo, useState } from 'react'
import api from '../services/api'
import './PatientAppointments.css'

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
  const [activeFilter, setActiveFilter] = useState('ALL')
  const [searchText, setSearchText] = useState('')

  const token = localStorage.getItem('token')
  const headers = { Authorization: `Bearer ${token}` }

  const getAppointments = async () => {
    try {
      const patientResponse = await api.get('/api/patients/me/dashboard', { headers })
      const id = patientResponse.data.patientId
      setPatientId(id)

      const response = await api.get(`/api/patients/${id}/appointments`, { headers })
      setAppointments(response.data)
      setMessage('')
    } catch (error) {
      setMessage(error.response?.data?.message || 'Unable to load appointments')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    getAppointments()
  }, [])

  const cancelAppointment = async (appointmentId) => {
    const confirmCancel = window.confirm('Are you sure you want to cancel this appointment?')
    if (!confirmCancel) return

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
      setMessage(error.response?.data?.message || 'Unable to cancel appointment')
    }
  }

  const checkInAppointment = async (appointmentId) => {
    try {
      setMessage('')
      setSuccess('')
      const response = await api.post(
        '/api/queue/patient/check-in',
        { appointmentId: appointmentId },
        { headers }
      )
      setSuccess(response.data.message + '. Your token number is ' + response.data.tokenNumber)
      setCheckedInAppointments(previous => [...previous, appointmentId])
    } catch (error) {
      setMessage(error.response?.data?.message || 'Unable to check in')
    }
  }

  const openReschedule = async (appointment) => {
    try {
      setMessage('')
      setSuccess('')
      setSelectedSlotId('')
      setReschedulingAppointment(appointment)

      const response = await api.get(`/api/doctors/${appointment.doctorId}/slots`, { headers })
      const today = new Date()
      const todayString =
        today.getFullYear() + '-' +
        String(today.getMonth() + 1).padStart(2, '0') + '-' +
        String(today.getDate()).padStart(2, '0')

      const slots = response.data.filter(
        slot => slot.status === 'AVAILABLE' && slot.slotDate >= todayString
      )
      setAvailableSlots(slots)
    } catch (error) {
      setReschedulingAppointment(null)
      setMessage(error.response?.data?.message || 'Unable to load available slots')
    }
  }

  const rescheduleAppointment = async (e) => {
    e.preventDefault()
    try {
      setMessage('')
      setSuccess('')
      const response = await api.put(
        `/api/appointments/${reschedulingAppointment.appointmentId}/reschedule`,
        { patientId: patientId, newSlotId: Number(selectedSlotId) },
        { headers }
      )
      setSuccess(response.data.message)
      setReschedulingAppointment(null)
      setAvailableSlots([])
      setSelectedSlotId('')
      await getAppointments()
    } catch (error) {
      setMessage(error.response?.data?.message || 'Unable to reschedule appointment')
    }
  }

  const formatDate = (dateValue) => {
    if (!dateValue) return 'Date not available'
    const date = new Date(dateValue + 'T00:00:00')
    if (Number.isNaN(date.getTime())) return dateValue
    return date.toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    })
  }

  const formatTime = (timeValue) => {
    if (!timeValue) return ''
    const parts = timeValue.split(':')
    const date = new Date()
    date.setHours(Number(parts[0]), Number(parts[1]), 0, 0)
    return date.toLocaleTimeString('en-IN', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    })
  }

  const upcomingCount = appointments.filter(
    item => item.status === 'BOOKED' || item.status === 'CONFIRMED'
  ).length
  const completedCount = appointments.filter(item => item.status === 'COMPLETED').length
  const cancelledCount = appointments.filter(item => item.status === 'CANCELLED').length

  const filteredAppointments = useMemo(() => {
    return appointments.filter(appointment => {
      const status = appointment.status
      let matchesFilter = true

      if (activeFilter === 'UPCOMING') {
        matchesFilter = status === 'BOOKED' || status === 'CONFIRMED'
      } else if (activeFilter === 'COMPLETED') {
        matchesFilter = status === 'COMPLETED'
      } else if (activeFilter === 'CANCELLED') {
        matchesFilter = status === 'CANCELLED'
      }

      const search = searchText.trim().toLowerCase()
      const matchesSearch = !search ||
        (appointment.doctorName || '').toLowerCase().includes(search) ||
        (appointment.clinicName || '').toLowerCase().includes(search)

      return matchesFilter && matchesSearch
    })
  }, [appointments, activeFilter, searchText])

  const filters = [
    { label: 'All', value: 'ALL' },
    { label: 'Upcoming', value: 'UPCOMING' },
    { label: 'Completed', value: 'COMPLETED' },
    { label: 'Cancelled', value: 'CANCELLED' }
  ]

  return (
    <div className="appointments-page">
      <div className="appointments-heading">
        <div>
          <div className="appointments-eyebrow">Patient portal</div>
          <h1 className="appointments-title">My Appointments</h1>
          <p className="appointments-subtitle">View and manage your healthcare visits in one place.</p>
        </div>
        <a className="appointments-new-button" href="/book-appointment">+ Book an appointment</a>
      </div>

      {message && <div className="appointments-alert error">{message}</div>}
      {success && <div className="appointments-alert success">{success}</div>}

      <div className="appointments-stats">
        <div className="appointments-stat">
          <div className="appointments-stat-number">{upcomingCount}</div>
          <div className="appointments-stat-label">Upcoming appointments</div>
        </div>
        <div className="appointments-stat">
          <div className="appointments-stat-number">{completedCount}</div>
          <div className="appointments-stat-label">Completed visits</div>
        </div>
        <div className="appointments-stat">
          <div className="appointments-stat-number">{cancelledCount}</div>
          <div className="appointments-stat-label">Cancelled appointments</div>
        </div>
      </div>

      <section className="appointments-panel">
        <div className="appointments-panel-head">
          <div>
            <h2 className="appointments-panel-title">Appointment history</h2>
            <p className="appointments-panel-caption">Your bookings and visit details</p>
          </div>
          <span className="appointments-panel-caption">{appointments.length} total</span>
        </div>

        <div className="appointments-tools">
          <input
            className="appointments-search"
            type="search"
            placeholder="Search doctor or clinic"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
          />
          <div className="appointments-filters">
            {filters.map(filter => (
              <button
                key={filter.value}
                type="button"
                className={`appointments-filter ${activeFilter === filter.value ? 'active' : ''}`}
                onClick={() => setActiveFilter(filter.value)}
              >
                {filter.label}
              </button>
            ))}
          </div>
        </div>

        {loading && <div className="appointments-loading">Loading your appointments...</div>}

        {!loading && filteredAppointments.length === 0 && (
          <div className="appointments-empty">
            <strong>{appointments.length === 0 ? 'No appointments yet' : 'No matching appointments'}</strong>
            {appointments.length === 0
              ? 'Your booked appointments will appear here.'
              : 'Try another filter or search term.'}
          </div>
        )}

        {!loading && filteredAppointments.length > 0 && (
          <div className="appointments-list">
            {filteredAppointments.map(appointment => {
              const canManage = appointment.status === 'BOOKED' || appointment.status === 'CONFIRMED'
              const checkedIn = checkedInAppointments.includes(appointment.appointmentId)

              return (
                <article className="appointment-item" key={appointment.appointmentId}>
                  <div className="appointment-main">
                    <div className="appointment-doctor-row">
                      <h3 className="appointment-doctor">{appointment.doctorName}</h3>
                      <span className={`appointment-status ${(appointment.status || '').toLowerCase()}`}>
                        {appointment.status}
                      </span>
                    </div>
                    <div className="appointment-clinic">{appointment.clinicName}</div>

                    <div className="appointment-meta">
                      <span><span className="appointment-meta-mark">▦</span>{formatDate(appointment.appointmentDate)}</span>
                      <span><span className="appointment-meta-mark">◷</span>{formatTime(appointment.startTime)} – {formatTime(appointment.endTime)}</span>
                    </div>

                    <div className="appointment-reason">
                      <strong>Reason:</strong> {appointment.reason || 'Not provided'}
                    </div>
                    <div className="appointment-id">Appointment #{appointment.appointmentId}</div>
                  </div>

                  <div className="appointment-side">
                    <div className="appointment-actions">
                      {canManage && (
                        <>
                          {checkedIn ? (
                            <span className="appointment-status confirmed">Checked in</span>
                          ) : (
                            <button
                              type="button"
                              className="appointment-action primary"
                              onClick={() => checkInAppointment(appointment.appointmentId)}
                            >
                              Check in
                            </button>
                          )}
                          <button
                            type="button"
                            className="appointment-action"
                            onClick={() => openReschedule(appointment)}
                          >
                            Reschedule
                          </button>
                          <button
                            type="button"
                            className="appointment-action danger"
                            onClick={() => cancelAppointment(appointment.appointmentId)}
                          >
                            Cancel
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </article>
              )
            })}
          </div>
        )}

        {reschedulingAppointment && (
          <div className="reschedule-panel">
            <h3>Reschedule appointment</h3>
            <p>{reschedulingAppointment.doctorName} · Current date: {formatDate(reschedulingAppointment.appointmentDate)}</p>
            <form className="reschedule-form" onSubmit={rescheduleAppointment}>
              <div className="reschedule-field">
                <label htmlFor="new-appointment-slot">Choose a new appointment slot</label>
                <select
                  id="new-appointment-slot"
                  className="appointments-select"
                  value={selectedSlotId}
                  onChange={(e) => setSelectedSlotId(e.target.value)}
                  required
                >
                  <option value="">Select an available time</option>
                  {availableSlots.map(slot => (
                    <option key={slot.slotId} value={slot.slotId}>
                      {formatDate(slot.slotDate)} · {formatTime(slot.startTime)} – {formatTime(slot.endTime)}
                    </option>
                  ))}
                </select>
              </div>
              <button type="submit" className="reschedule-submit" disabled={!selectedSlotId}>
                Confirm reschedule
              </button>
              <button
                type="button"
                className="reschedule-close"
                onClick={() => {
                  setReschedulingAppointment(null)
                  setAvailableSlots([])
                  setSelectedSlotId('')
                }}
              >
                Close
              </button>
            </form>
            {availableSlots.length === 0 && (
              <p style={{ marginTop: '12px', marginBottom: 0 }}>No upcoming available slots found for this doctor.</p>
            )}
          </div>
        )}
      </section>

      <a className="appointments-back" href="/patient-dashboard">← Back to dashboard</a>
    </div>
  )
}

export default PatientAppointments
