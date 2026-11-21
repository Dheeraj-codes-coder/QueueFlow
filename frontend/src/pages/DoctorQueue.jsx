
import { useCallback, useEffect, useState } from 'react'
import api from '../services/api'

function DoctorQueue() {

  const [queue, setQueue] = useState([])
  const [doctorId, setDoctorId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')

  const getQueue = useCallback(async () => {
    try {
      setLoading(true)
      setMessage('')

      const token = localStorage.getItem('token')
      const user = JSON.parse(localStorage.getItem('user'))

      const doctorsResponse = await api.get('/api/doctors', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })

      const doctor = doctorsResponse.data.find(
        item =>
          item.fullName?.trim().toLowerCase() ===
          user?.fullName?.trim().toLowerCase()
      )

      if (!doctor) {
        setMessage('Doctor profile not found for this account.')
        setQueue([])
        return
      }

      setDoctorId(doctor.doctorId)

      const response = await api.get(
        `/api/queue/doctors/${doctor.doctorId}/today`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      setQueue(response.data)

    } catch (error) {
      setQueue([])
      setMessage(
        error.response?.data?.message || 'Unable to load queue'
      )
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    getQueue()
  }, [getQueue])

  const callNextPatient = async () => {
    if (!doctorId) return

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
      await getQueue()

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
      await getQueue()

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to start consultation'
      )
    }
  }

  const completeConsultation = async (queueTicketId) => {
    const notes = window.prompt('Enter consultation notes:')

    if (notes === null) return

    try {
      setMessage('')
      setSuccess('')

      const response = await api.post(
        `/api/queue/tickets/${queueTicketId}/complete-consultation`,
        { notes: notes },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        }
      )

      setSuccess(response.data.message || 'Consultation completed successfully')
      await getQueue()

    } catch (error) {
      setMessage(
        error.response?.data?.message || 'Unable to complete consultation'
      )
    }
  }

  const waitingCount = queue.filter(
    ticket => ticket.status === 'WAITING'
  ).length

  const calledCount = queue.filter(
    ticket => ticket.status === 'CALLED'
  ).length

  const consultingCount = queue.filter(
    ticket => ticket.status === 'IN_CONSULTATION'
  ).length

  const getStatusStyle = (status) => {
    if (status === 'WAITING') {
      return { background: '#fff4d6', color: '#9a6700' }
    }

    if (status === 'CALLED') {
      return { background: '#dbeafe', color: '#1d4ed8' }
    }

    if (status === 'IN_CONSULTATION') {
      return { background: '#ede9fe', color: '#6d28d9' }
    }

    if (status === 'COMPLETED') {
      return { background: '#dcfce7', color: '#15803d' }
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
              DOCTOR WORKSPACE / QUEUE
            </p>

            <h2 style={{
              color: '#172554',
              fontWeight: '750',
              marginBottom: '5px'
            }}>
              Queue Management
            </h2>

            <p style={{ color: '#64748b', margin: 0 }}>
              Manage patient check-ins and consultation progress.
            </p>
          </div>

          <div className="d-flex gap-2 flex-wrap">

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
              onClick={getQueue}
              disabled={loading}
            >
              ↻ Refresh
            </button>

            <button
              className="btn"
              style={{
                background: '#2563eb',
                color: '#ffffff',
                borderRadius: '9px',
                padding: '10px 18px',
                fontWeight: '600'
              }}
              onClick={callNextPatient}
              disabled={!doctorId || loading}
            >
              Call Next Patient →
            </button>

          </div>
        </div>

        {/* Alerts */}

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

        {/* Summary */}

        <div className="row g-3 mb-4">

          <div className="col-12 col-md-4">
            <div style={cardStyle}>
              <p style={{ color: '#64748b', marginBottom: '10px' }}>
                Waiting Patients
              </p>

              <h2 style={{ color: '#d97706', fontWeight: '750', margin: 0 }}>
                {loading ? '—' : waitingCount}
              </h2>

              <small style={{ color: '#94a3b8' }}>
                Awaiting their turn
              </small>
            </div>
          </div>

          <div className="col-12 col-md-4">
            <div style={cardStyle}>
              <p style={{ color: '#64748b', marginBottom: '10px' }}>
                Patients Called
              </p>

              <h2 style={{ color: '#2563eb', fontWeight: '750', margin: 0 }}>
                {loading ? '—' : calledCount}
              </h2>

              <small style={{ color: '#94a3b8' }}>
                Ready for consultation
              </small>
            </div>
          </div>

          <div className="col-12 col-md-4">
            <div style={cardStyle}>
              <p style={{ color: '#64748b', marginBottom: '10px' }}>
                In Consultation
              </p>

              <h2 style={{ color: '#7c3aed', fontWeight: '750', margin: 0 }}>
                {loading ? '—' : consultingCount}
              </h2>

              <small style={{ color: '#94a3b8' }}>
                Currently being attended
              </small>
            </div>
          </div>

        </div>

        {/* Queue Table */}

        <div style={cardStyle}>

          <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">

            <div>
              <h5 style={{
                color: '#172554',
                fontWeight: '700',
                marginBottom: '5px'
              }}>
                Today's Patient Queue
              </h5>

              <p style={{
                color: '#94a3b8',
                fontSize: '13px',
                margin: 0
              }}>
                Track and update patient consultation status.
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
              {loading ? 'Loading...' : `${queue.length} Patients`}
            </span>

          </div>

          {loading && (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status" />
              <p className="mt-3 text-muted">Loading patient queue...</p>
            </div>
          )}

          {!loading && !message && queue.length === 0 && (
            <div className="text-center py-5">

              <div style={{
                fontSize: '42px',
                marginBottom: '12px'
              }}>
                📋
              </div>

              <h5 style={{ color: '#334155', fontWeight: '650' }}>
                No patients in the queue
              </h5>

              <p style={{ color: '#94a3b8', margin: 0 }}>
                Patients who check in today will appear here.
              </p>

            </div>
          )}

          {!loading && !message && queue.length > 0 && (
            <div className="table-responsive">

              <table className="table align-middle mb-0">

                <thead>
                  <tr style={{
                    background: '#f8fafc',
                    color: '#64748b',
                    fontSize: '12px',
                    textTransform: 'uppercase'
                  }}>
                    <th className="py-3">Token</th>
                    <th>Patient</th>
                    <th>Check-in Time</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>

                  {queue.map((ticket) => (

                    <tr key={ticket.queueTicketId}>

                      <td>
                        <span style={{
                          background: '#eff6ff',
                          color: '#1d4ed8',
                          padding: '7px 12px',
                          borderRadius: '8px',
                          fontWeight: '700'
                        }}>
                          #{ticket.tokenNumber}
                        </span>
                      </td>

                      <td style={{
                        color: '#1e293b',
                        fontWeight: '600'
                      }}>
                        {ticket.patientName}
                      </td>

                      <td style={{ color: '#64748b' }}>
                        {ticket.checkInTime
                          ? new Date(ticket.checkInTime).toLocaleTimeString()
                          : '-'}
                      </td>

                      <td>
                        <span style={{
                          ...getStatusStyle(ticket.status),
                          padding: '6px 11px',
                          borderRadius: '20px',
                          fontSize: '12px',
                          fontWeight: '700',
                          whiteSpace: 'nowrap'
                        }}>
                          {ticket.status?.replaceAll('_', ' ')}
                        </span>
                      </td>

                      <td>

                        {ticket.status === 'CALLED' && (
                          <button
                            className="btn btn-sm"
                            style={{
                              background: '#2563eb',
                              color: '#ffffff',
                              borderRadius: '7px',
                              fontWeight: '600'
                            }}
                            onClick={() => startConsultation(ticket.queueTicketId)}
                          >
                            Start Consultation
                          </button>
                        )}

                        {ticket.status === 'IN_CONSULTATION' && (
                          <button
                            className="btn btn-sm"
                            style={{
                              background: '#16a34a',
                              color: '#ffffff',
                              borderRadius: '7px',
                              fontWeight: '600'
                            }}
                            onClick={() => completeConsultation(ticket.queueTicketId)}
                          >
                            Complete
                          </button>
                        )}

                        {ticket.status !== 'CALLED' &&
                          ticket.status !== 'IN_CONSULTATION' && (
                            <span style={{ color: '#94a3b8' }}>—</span>
                          )}

                      </td>

                    </tr>

                  ))}

                </tbody>

              </table>

            </div>
          )}

        </div>

        {/* Back Button */}

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

export default DoctorQueue
