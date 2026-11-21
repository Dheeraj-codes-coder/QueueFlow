
import { useEffect, useState } from 'react'
import api from '../services/api'
import './BookAppointment.css'

function BookAppointment() {

  const [patientId, setPatientId] = useState(null)
  const [doctors, setDoctors] = useState([])
  const [doctorId, setDoctorId] = useState('')
  const [slots, setSlots] = useState([])
  const [slotId, setSlotId] = useState('')
  const [reason, setReason] = useState('')

  const [loading, setLoading] = useState(true)
  const [slotsLoading, setSlotsLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')

  const getData = async () => {

    try {

      const token = localStorage.getItem('token')

      const headers = {
        Authorization: `Bearer ${token}`
      }

      const patientResponse = await api.get(
        '/api/patients/me/dashboard',
        { headers: headers }
      )

      setPatientId(patientResponse.data.patientId)

      const doctorResponse = await api.get(
        '/api/doctors',
        { headers: headers }
      )

      setDoctors(doctorResponse.data)

      if (doctorResponse.data.length === 0) {
        setMessage('No doctors are currently available')
      }

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to load booking details'
      )

    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    getData()
  }, [])

  const getSlots = async (id) => {

    try {

      setSlotsLoading(true)

      const response = await api.get(
        `/api/doctors/${id}/slots`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setSlots(response.data)
      setMessage('')

    } catch (error) {

      setSlots([])

      setMessage(
        error.response?.data?.message ||
        'Unable to load appointment slots'
      )

    } finally {
      setSlotsLoading(false)
    }
  }

  const handleDoctorChange = (e) => {

    const selectedId = e.target.value

    setDoctorId(selectedId)
    setSlotId('')
    setSlots([])
    setMessage('')
    setSuccess('')

    if (selectedId) {
      getSlots(selectedId)
    }
  }

  const bookAppointment = async (e) => {

    e.preventDefault()

    setMessage('')
    setSuccess('')

    try {

      const response = await api.post(
        '/api/appointments/book',
        {
          patientId: patientId,
          slotId: Number(slotId),
          reason: reason
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setSuccess(response.data.message)
      setSlotId('')
      setReason('')

      await getSlots(doctorId)

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to book appointment'
      )
    }
  }

  const today = new Date().toLocaleDateString('en-CA')

  const availableSlots = slots
    .filter(
      (slot) =>
        slot.status === 'AVAILABLE' &&
        slot.slotDate >= today
    )
    .sort((a, b) => {

      const dateCompare = a.slotDate.localeCompare(b.slotDate)

      if (dateCompare !== 0) {
        return dateCompare
      }

      return a.startTime.localeCompare(b.startTime)
    })

  const selectedSlot = availableSlots.find(
    (slot) => String(slot.slotId) === slotId
  )

  const selectedDoctor = doctors.find(
    (doctor) => String(doctor.doctorId) === doctorId
  )

  return (

    <div className="booking-page">

      <div className="booking-container">

        <header className="booking-header">

          <div>
            <div className="booking-brand">
              <span>+</span> QueueFlow
            </div>

            <h2>Book an Appointment</h2>

            <p>
              Schedule your consultation with just a few simple steps.
            </p>
          </div>

          <button
            className="booking-back-button"
            onClick={() => window.location.href = '/patient-dashboard'}
          >
            ← Dashboard
          </button>

        </header>

        <div className="booking-banner">

          <div>
            <span className="booking-banner-label">
              YOUR HEALTH COMES FIRST
            </span>

            <h3>Let's get you scheduled.</h3>

            <p>
              Choose your preferred doctor, select an available
              consultation time and tell us what you need help with.
            </p>
          </div>

          <div className="booking-banner-icon">
            ✚
          </div>

        </div>

        {message && (
          <div className="booking-alert booking-alert-error">
            <span>⚠</span> {message}
          </div>
        )}

        {success && (
          <div className="booking-alert booking-alert-success">
            <span>✓</span> {success}
          </div>
        )}

        {loading ? (

          <div className="booking-loading">
            <div className="spinner-border text-primary"></div>
            <p>Loading doctors and booking details...</p>
          </div>

        ) : (

          <div className="booking-content">

            <div className="booking-form-card">

              <div className="booking-card-heading">

                <div className="booking-heading-icon">▣</div>

                <div>
                  <h4>Appointment Details</h4>
                  <p>Complete the information below.</p>
                </div>

              </div>

              <form onSubmit={bookAppointment}>

                <div className="booking-field">

                  <label>Select Doctor</label>

                  <select
                    value={doctorId}
                    onChange={handleDoctorChange}
                    required
                  >

                    <option value="">
                      Choose a doctor
                    </option>

                    {doctors.map((doctor) => (
                      <option
                        key={doctor.doctorId}
                        value={doctor.doctorId}
                      >
                        {doctor.fullName}
                        {doctor.specialty
                          ? ` - ${doctor.specialty}`
                          : ''}
                      </option>
                    ))}

                  </select>

                  <small>
                    Select the doctor you want to consult.
                  </small>

                </div>

                {selectedDoctor && (
                  <div className="booking-selected-slot">

                    <span>✓</span>

                    <div>
                      <strong>{selectedDoctor.fullName}</strong>

                      <p>
                        {selectedDoctor.specialty || 'General Consultation'}
                      </p>

                      {selectedDoctor.qualification && (
                        <p>{selectedDoctor.qualification}</p>
                      )}

                    </div>

                  </div>
                )}

                <div className="booking-field">

                  <label>Available Time Slot</label>

                  <select
                    value={slotId}
                    onChange={(e) => setSlotId(e.target.value)}
                    required
                    disabled={!doctorId || slotsLoading}
                  >

                    <option value="">
                      {!doctorId
                        ? 'Select a doctor first'
                        : slotsLoading
                          ? 'Loading available slots...'
                          : 'Select an available time'}
                    </option>

                    {availableSlots.map((slot) => (
                      <option
                        key={slot.slotId}
                        value={slot.slotId}
                      >
                        {slot.slotDate} | {slot.startTime} - {slot.endTime}
                      </option>
                    ))}

                  </select>

                  <small>
                    Only available upcoming slots are displayed.
                  </small>

                </div>

                {slotsLoading && (
                  <p className="text-primary small">
                    Loading doctor's available slots...
                  </p>
                )}

                {selectedSlot && (
                  <div className="booking-selected-slot">

                    <span>✓</span>

                    <div>
                      <strong>Selected Appointment</strong>
                      <p>{selectedSlot.slotDate}</p>
                      <p>
                        {selectedSlot.startTime} - {selectedSlot.endTime}
                      </p>
                    </div>

                  </div>
                )}

                <div className="booking-field">

                  <label>Reason for Appointment</label>

                  <textarea
                    rows="4"
                    placeholder="Describe your symptoms or reason for visiting..."
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    required
                  />

                </div>

                <button
                  type="submit"
                  className="booking-submit"
                  disabled={!slotId || !patientId || slotsLoading}
                >
                  Confirm Appointment <span>→</span>
                </button>

              </form>

            </div>

            <div className="booking-side-panel">

              <div className="booking-info-card">

                <div className="booking-info-icon">📅</div>

                <h4>Available Slots</h4>

                <div className="booking-slot-count">
                  {availableSlots.length}
                </div>

                <p>
                  Upcoming available slots
                  {selectedDoctor
                    ? ` for ${selectedDoctor.fullName}`
                    : '. Select a doctor to see their slots.'}
                </p>

              </div>

              <div className="booking-tips-card">

                <h4>Before You Book</h4>

                <div className="booking-tip">
                  <span>✓</span>
                  Choose your preferred doctor.
                </div>

                <div className="booking-tip">
                  <span>✓</span>
                  Select an available time slot.
                </div>

                <div className="booking-tip">
                  <span>✓</span>
                  Enter your reason for consultation.
                </div>

                <div className="booking-tip">
                  <span>✓</span>
                  Confirm your appointment.
                </div>

              </div>

              <div className="booking-help-card">
                <span>♥</span>

                <div>
                  <strong>Your health matters</strong>
                  <p>
                    Keep your appointments and medical information
                    organized with QueueFlow.
                  </p>
                </div>

              </div>

            </div>

          </div>

        )}

        {!loading && doctorId && !slotsLoading &&
          availableSlots.length === 0 && (
            <div className="booking-no-slots">
              <span>📅</span>
              <h4>No Upcoming Slots Available</h4>
              <p>
                This doctor currently has no available upcoming
                appointments. Try selecting another doctor.
              </p>
            </div>
          )}

        <footer className="booking-footer">
          QueueFlow · Healthcare management made simpler.
        </footer>

      </div>

    </div>
  )
}

export default BookAppointment
