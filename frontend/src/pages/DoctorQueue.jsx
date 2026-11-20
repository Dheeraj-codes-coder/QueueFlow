
import { useEffect, useState } from 'react'
import api from '../services/api'

function DoctorQueue() {

  const [queue, setQueue] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')

  const doctorId = 1

  const getQueue = async () => {
    try {
      const response = await api.get(
        `/api/queue/doctors/${doctorId}/today`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setQueue(response.data)
      setMessage('')

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to load queue'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    getQueue()
  }, [])

  const callNextPatient = async () => {
    try {
      setMessage('')
      setSuccess('')

      const response = await api.post(
        `/api/queue/doctors/${doctorId}/call-next`,
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setSuccess(response.data.message || 'Next patient called successfully')

      getQueue()

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to call next patient'
      )
    }
  }

  const startConsultation = async (queueTicketId) => {
    try {
      setMessage('')
      setSuccess('')

      const response = await api.post(
        `/api/queue/tickets/${queueTicketId}/start-consultation`,
        {},
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setSuccess(response.data.message || 'Consultation started successfully')

      getQueue()

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to start consultation'
      )
    }
  }

  const completeConsultation = async (queueTicketId) => {

    const notes = window.prompt('Enter consultation notes:')

    if (notes === null) {
      return
    }

    try {
      setMessage('')
      setSuccess('')

      const response = await api.post(
        `/api/queue/tickets/${queueTicketId}/complete-consultation`,
        {
          notes: notes
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setSuccess(response.data.message || 'Consultation completed successfully')

      getQueue()

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to complete consultation'
      )
    }
  }

  return (
    <div className="container mt-4">

      <div className="card shadow p-4">

        <h2>QueueFlow</h2>

        <h4 className="mt-3">Queue Management</h4>

        <p>Manage your patients waiting for consultation.</p>

        {message && (
          <div className="alert alert-danger">
            {message}
          </div>
        )}

        {success && (
          <div className="alert alert-success">
            {success}
          </div>
        )}

        <div className="d-flex gap-2 mb-3">

          <button
            className="btn btn-primary"
            onClick={callNextPatient}
          >
            Call Next Patient
          </button>

          <button
            className="btn btn-outline-primary"
            onClick={getQueue}
          >
            Refresh Queue
          </button>

        </div>

        {loading && <p>Loading queue...</p>}

        {!loading && queue.length === 0 && (
          <div className="alert alert-info">
            No patients are currently in today's queue.
          </div>
        )}

        {!loading && queue.length > 0 && (

          <div className="table-responsive">

            <table className="table table-bordered table-striped">

              <thead>
                <tr>
                  <th>Token</th>
                  <th>Patient Name</th>
                  <th>Check-in Time</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>

                {queue.map((ticket) => (

                  <tr key={ticket.queueTicketId}>

                    <td>{ticket.tokenNumber}</td>

                    <td>{ticket.patientName}</td>

                    <td>
                      {ticket.checkInTime
                        ? new Date(ticket.checkInTime).toLocaleTimeString()
                        : '-'}
                    </td>

                    <td>{ticket.status}</td>

                    <td>

                      {ticket.status === 'CALLED' && (
                        <button
                          className="btn btn-primary btn-sm"
                          onClick={() => startConsultation(ticket.queueTicketId)}
                        >
                          Start Consultation
                        </button>
                      )}

                      {ticket.status === 'IN_CONSULTATION' && (
                        <button
                          className="btn btn-success btn-sm"
                          onClick={() => completeConsultation(ticket.queueTicketId)}
                        >
                          Complete Consultation
                        </button>
                      )}

                      {ticket.status !== 'CALLED' &&
                        ticket.status !== 'IN_CONSULTATION' && (
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
          onClick={() => window.location.href = '/doctor-dashboard'}
        >
          Back to Dashboard
        </button>

      </div>

    </div>
  )
}

export default DoctorQueue
