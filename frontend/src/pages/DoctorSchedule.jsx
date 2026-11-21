
import { useEffect, useState } from 'react'
import api from '../services/api'

function DoctorSchedule() {

  const user = JSON.parse(localStorage.getItem('user'))

  const [doctorId, setDoctorId] = useState(null)
  const [schedules, setSchedules] = useState([])

  const [dayOfWeek, setDayOfWeek] = useState('MONDAY')
  const [startTime, setStartTime] = useState('09:00')
  const [endTime, setEndTime] = useState('17:00')

  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [success, setSuccess] = useState('')
  const [editId, setEditId] = useState(null)

  const token = localStorage.getItem('token')

  const headers = {
    Authorization: `Bearer ${token}`
  }

  useEffect(() => {
    findDoctor()
  }, [])

  const findDoctor = async () => {

    try {

      const response = await api.get('/api/doctors', {
        headers: headers
      })

      const doctor = response.data.find(
        (item) => item.fullName === user?.fullName
      )

      if (!doctor) {
        setMessage('Doctor profile not found for this account')
        setLoading(false)
        return
      }

      setDoctorId(doctor.doctorId)
      getSchedules(doctor.doctorId)

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to find doctor profile'
      )

      setLoading(false)
    }
  }

  const getSchedules = async (id) => {

    try {

      const response = await api.get(
        `/api/doctors/${id}/schedule`,
        {
          headers: headers
        }
      )

      setSchedules(response.data)
      setMessage('')

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to load schedules'
      )

    } finally {

      setLoading(false)
    }
  }

  const saveSchedule = async (event) => {

    event.preventDefault()

    setMessage('')
    setSuccess('')

    if (!doctorId) {
      setMessage('Doctor profile is not loaded')
      return
    }

    if (startTime >= endTime) {
      setMessage('End time must be after start time')
      return
    }

    try {

      if (editId === null) {

        await api.post(
          `/api/doctors/${doctorId}/schedule`,
          {
            dayOfWeek: dayOfWeek,
            startTime: startTime,
            endTime: endTime,
            available: true
          },
          {
            headers: headers
          }
        )

        setSuccess('Schedule added successfully')

      } else {

        await api.put(
          `/api/doctors/schedule/${editId}`,
          {
            startTime: startTime,
            endTime: endTime,
            available: true
          },
          {
            headers: headers
          }
        )

        setSuccess('Schedule updated successfully')
      }

      setEditId(null)
      setDayOfWeek('MONDAY')
      setStartTime('09:00')
      setEndTime('17:00')

      getSchedules(doctorId)

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to save schedule'
      )
    }
  }

  const editSchedule = (schedule) => {

    setEditId(schedule.scheduleId)
    setDayOfWeek(schedule.dayOfWeek)
    setStartTime(schedule.startTime.substring(0, 5))
    setEndTime(schedule.endTime.substring(0, 5))

    setMessage('')
    setSuccess('')

    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  const deleteSchedule = async (scheduleId) => {

    const confirmDelete = window.confirm(
      'Are you sure you want to delete this schedule?'
    )

    if (!confirmDelete) {
      return
    }

    setMessage('')
    setSuccess('')

    try {

      await api.delete(
        `/api/doctors/schedule/${scheduleId}`,
        {
          headers: headers
        }
      )

      setSuccess('Schedule deleted successfully')
      getSchedules(doctorId)

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to delete schedule'
      )
    }
  }

  const cancelEdit = () => {

    setEditId(null)
    setDayOfWeek('MONDAY')
    setStartTime('09:00')
    setEndTime('17:00')
    setMessage('')
    setSuccess('')

  }

  const formatTime = (time) => {

    if (!time) {
      return ''
    }

    const [hours, minutes] = time.split(':')
    const date = new Date()

    date.setHours(Number(hours), Number(minutes))

    return date.toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const availableCount = schedules.filter(
    (schedule) => schedule.available
  ).length

  const unavailableCount = schedules.length - availableCount

  const days = [
    'MONDAY',
    'TUESDAY',
    'WEDNESDAY',
    'THURSDAY',
    'FRIDAY',
    'SATURDAY',
    'SUNDAY'
  ]

  return (

    <div style={{
      minHeight: '100vh',
      background: '#f5f7fb',
      padding: '30px 15px'
    }}>

      <div className="container" style={{ maxWidth: '1150px' }}>

        {/* Header */}

        <div className="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">

          <div>
            <div style={{
              color: '#64748b',
              fontSize: '13px',
              fontWeight: '600',
              letterSpacing: '1px'
            }}>
              QUEUEFLOW / DOCTOR
            </div>

            <h2 className="fw-bold mt-2 mb-1" style={{ color: '#172554' }}>
              Schedule Management
            </h2>

            <p className="text-secondary mb-0">
              Manage your weekly working days and consultation timings.
            </p>
          </div>

          <button
            className="btn btn-light border"
            onClick={() => window.location.href = '/doctor-dashboard'}
            style={{
              borderRadius: '10px',
              padding: '10px 18px',
              fontWeight: '600'
            }}
          >
            ← Dashboard
          </button>

        </div>

        {/* Messages */}

        {message && (
          <div className="alert alert-danger border-0 shadow-sm">
            {message}
          </div>
        )}

        {success && (
          <div className="alert alert-success border-0 shadow-sm">
            {success}
          </div>
        )}

        {/* Summary Cards */}

        <div className="row g-3 mb-4">

          <div className="col-md-4">
            <div className="card border-0 shadow-sm h-100"
              style={{ borderRadius: '14px' }}>
              <div className="card-body p-4">
                <div className="text-secondary small fw-semibold">
                  TOTAL SCHEDULES
                </div>
                <h2 className="fw-bold mt-2 mb-0" style={{ color: '#1e293b' }}>
                  {schedules.length}
                </h2>
                <div className="text-secondary small mt-2">
                  Weekly working slots
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card border-0 shadow-sm h-100"
              style={{ borderRadius: '14px' }}>
              <div className="card-body p-4">
                <div className="text-secondary small fw-semibold">
                  AVAILABLE
                </div>
                <h2 className="fw-bold mt-2 mb-0 text-success">
                  {availableCount}
                </h2>
                <div className="text-secondary small mt-2">
                  Active schedules
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card border-0 shadow-sm h-100"
              style={{ borderRadius: '14px' }}>
              <div className="card-body p-4">
                <div className="text-secondary small fw-semibold">
                  UNAVAILABLE
                </div>
                <h2 className="fw-bold mt-2 mb-0 text-danger">
                  {unavailableCount}
                </h2>
                <div className="text-secondary small mt-2">
                  Inactive schedules
                </div>
              </div>
            </div>
          </div>

        </div>

        {/* Schedule Form */}

        <div className="card border-0 shadow-sm mb-4"
          style={{ borderRadius: '16px' }}>

          <div className="card-body p-4">

            <div className="d-flex align-items-center gap-3 mb-4">

              <div style={{
                width: '45px',
                height: '45px',
                background: '#e0e7ff',
                color: '#4338ca',
                borderRadius: '12px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '22px'
              }}>
                +
              </div>

              <div>
                <h5 className="fw-bold mb-1">
                  {editId === null ? 'Add Working Schedule' : 'Edit Working Schedule'}
                </h5>

                <p className="text-secondary small mb-0">
                  Set your consultation day and working hours.
                </p>
              </div>

            </div>

            <form onSubmit={saveSchedule}>

              <div className="row g-3">

                <div className="col-md-4">

                  <label className="form-label fw-semibold">
                    Day of Week
                  </label>

                  <select
                    className="form-select"
                    value={dayOfWeek}
                    onChange={(event) => setDayOfWeek(event.target.value)}
                    disabled={editId !== null}
                    style={{
                      padding: '12px',
                      borderRadius: '10px'
                    }}
                  >
                    {days.map((day) => (
                      <option key={day} value={day}>
                        {day.charAt(0) + day.slice(1).toLowerCase()}
                      </option>
                    ))}
                  </select>

                </div>

                <div className="col-md-3">

                  <label className="form-label fw-semibold">
                    Start Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    value={startTime}
                    onChange={(event) => setStartTime(event.target.value)}
                    required
                    style={{
                      padding: '12px',
                      borderRadius: '10px'
                    }}
                  />

                </div>

                <div className="col-md-3">

                  <label className="form-label fw-semibold">
                    End Time
                  </label>

                  <input
                    type="time"
                    className="form-control"
                    value={endTime}
                    onChange={(event) => setEndTime(event.target.value)}
                    required
                    style={{
                      padding: '12px',
                      borderRadius: '10px'
                    }}
                  />

                </div>

                <div className="col-md-2 d-flex align-items-end">

                  <button
                    type="submit"
                    className="btn btn-primary w-100"
                    style={{
                      padding: '12px',
                      borderRadius: '10px',
                      fontWeight: '600'
                    }}
                  >
                    {editId === null ? '＋ Add' : 'Update'}
                  </button>

                </div>

              </div>

              {editId !== null && (
                <button
                  type="button"
                  className="btn btn-light border mt-3"
                  onClick={cancelEdit}
                >
                  Cancel Edit
                </button>
              )}

            </form>

          </div>
        </div>

        {/* Weekly Schedule Table */}

        <div className="card border-0 shadow-sm"
          style={{ borderRadius: '16px' }}>

          <div className="card-body p-4">

            <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">

              <div>
                <h5 className="fw-bold mb-1">
                  Weekly Schedules
                </h5>

                <p className="text-secondary small mb-0">
                  Your configured consultation timings.
                </p>
              </div>

              <button
                className="btn btn-light border"
                onClick={() => doctorId && getSchedules(doctorId)}
                disabled={loading}
                style={{
                  borderRadius: '9px',
                  fontWeight: '600'
                }}
              >
                ↻ Refresh
              </button>

            </div>

            {loading && (
              <div className="text-center py-5">
                <div className="spinner-border text-primary"></div>
                <p className="text-secondary mt-3 mb-0">
                  Loading schedules...
                </p>
              </div>
            )}

            {!loading && schedules.length === 0 && (
              <div className="text-center py-5">

                <div style={{ fontSize: '42px' }}>
                  📅
                </div>

                <h6 className="fw-bold mt-3">
                  No schedules found
                </h6>

                <p className="text-secondary mb-0">
                  Add your first working schedule using the form above.
                </p>

              </div>
            )}

            {!loading && schedules.length > 0 && (

              <div className="table-responsive">

                <table className="table align-middle">

                  <thead>
                    <tr style={{
                      background: '#f8fafc',
                      color: '#64748b'
                    }}>
                      <th className="py-3">DAY</th>
                      <th>START TIME</th>
                      <th>END TIME</th>
                      <th>AVAILABILITY</th>
                      <th className="text-end">ACTIONS</th>
                    </tr>
                  </thead>

                  <tbody>

                    {schedules.map((schedule) => (

                      <tr key={schedule.scheduleId}>

                        <td className="py-3">
                          <span className="fw-semibold">
                            {schedule.dayOfWeek.charAt(0) +
                              schedule.dayOfWeek.slice(1).toLowerCase()}
                          </span>
                        </td>

                        <td>
                          {formatTime(schedule.startTime)}
                        </td>

                        <td>
                          {formatTime(schedule.endTime)}
                        </td>

                        <td>
                          {schedule.available ? (
                            <span className="badge rounded-pill bg-success-subtle text-success px-3 py-2">
                              ● Available
                            </span>
                          ) : (
                            <span className="badge rounded-pill bg-danger-subtle text-danger px-3 py-2">
                              ● Unavailable
                            </span>
                          )}
                        </td>

                        <td className="text-end">

                          <button
                            className="btn btn-sm btn-outline-primary me-2"
                            onClick={() => editSchedule(schedule)}
                            style={{ borderRadius: '8px' }}
                          >
                            Edit
                          </button>

                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => deleteSchedule(schedule.scheduleId)}
                            style={{ borderRadius: '8px' }}
                          >
                            Delete
                          </button>

                        </td>

                      </tr>

                    ))}

                  </tbody>

                </table>

              </div>

            )}

          </div>
        </div>

        <div className="text-center text-secondary small mt-4">
          QueueFlow · Doctor Schedule Management
        </div>

      </div>

    </div>
  )
}

export default DoctorSchedule
