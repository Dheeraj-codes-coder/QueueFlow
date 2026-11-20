
import { useEffect, useState } from 'react'
import api from '../services/api'

function BookAppointment() {

  const [patientId, setPatientId] = useState(null)
  const [slots, setSlots] = useState([])
  const [slotId, setSlotId] = useState('')
  const [reason, setReason] = useState('')
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')

  const doctorId = 1

  const getData = async () => {
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

      setPatientId(patientResponse.data.patientId)

      const slotResponse = await api.get(
        `/api/doctors/${doctorId}/slots`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      setSlots(slotResponse.data)
      setMessage('')

    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        'Unable to load appointment slots'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    getData()
  }, [])

  const bookAppointment = async (e) => {
    e.preventDefault()

    try {
      setMessage('')
      setSuccess('')

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

      await getData()

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

  return (
    <div className="container mt-4">
      <div className="card shadow p-4">

        <h2>QueueFlow</h2>

        <h4 className="mt-3">Book Appointment</h4>

        <p>Select an available time slot to book your appointment.</p>

        {message && (
          <div className="alert alert-danger">{message}</div>
        )}

        {success && (
          <div className="alert alert-success">{success}</div>
        )}

        {loading && <p>Loading available slots...</p>}

        {!loading && (
          <>
            <form onSubmit={bookAppointment}>

              <div className="mb-3">
                <label className="form-label">Doctor</label>
                <input
                  type="text"
                  className="form-control"
                  value="Doctor ID: 1"
                  disabled
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Available Time Slot</label>

                <select
                  className="form-select"
                  value={slotId}
                  onChange={(e) => setSlotId(e.target.value)}
                  required
                >
                  <option value="">Select a time slot</option>

                  {availableSlots.map((slot) => (
                    <option
                      key={slot.slotId}
                      value={slot.slotId}
                    >
                      {slot.slotDate} | {slot.startTime} - {slot.endTime}
                    </option>
                  ))}
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label">Reason for Appointment</label>

                <textarea
                  className="form-control"
                  rows="3"
                  placeholder="Enter your reason"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  required
                />
              </div>

              <button
                type="submit"
                className="btn btn-primary"
                disabled={!slotId || !patientId}
              >
                Book Appointment
              </button>

            </form>

            {availableSlots.length === 0 && (
              <div className="alert alert-info mt-3">
                No upcoming available slots found.
              </div>
            )}
          </>
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

export default BookAppointment
