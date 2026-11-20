
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'

function AdminDashboard() {

  const navigate = useNavigate()

  const [doctors, setDoctors] = useState([])
  const [specialties, setSpecialties] = useState([])
  const [selectedSpecialties, setSelectedSpecialties] = useState({})
  const [search, setSearch] = useState('')
  const [filterSpecialty, setFilterSpecialty] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(true)
  const [savingId, setSavingId] = useState(null)

  // User Management states
  const [users, setUsers] = useState([])
  const [userSearch, setUserSearch] = useState('')
  const [userRoleFilter, setUserRoleFilter] = useState('')
  const [userStatusFilter, setUserStatusFilter] = useState('')
  const [userLoading, setUserLoading] = useState(true)
  const [userSavingId, setUserSavingId] = useState(null)
  const [userMessage, setUserMessage] = useState('')

  const token = localStorage.getItem('token')

  const getHeaders = () => {
    return {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  }

  useEffect(() => {

    const user = JSON.parse(localStorage.getItem('user') || '{}')

    if (!token || user.role !== 'ADMIN') {
      navigate('/', { replace: true })
      return
    }

    loadDashboard()
    loadUsers()

  }, [])

  const loadDashboard = async () => {

    try {

      setLoading(true)

      const doctorResponse = await api.get(
        '/api/doctors',
        getHeaders()
      )

      const specialtyResponse = await api.get(
        '/api/doctors/specialties',
        getHeaders()
      )

      setDoctors(doctorResponse.data)
      setSpecialties(specialtyResponse.data)

      const selected = {}

      doctorResponse.data.forEach((doctor) => {
        selected[doctor.doctorId] = doctor.specialty || ''
      })

      setSelectedSpecialties(selected)

      setMessage('')

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to load admin dashboard.'
      )

    } finally {

      setLoading(false)

    }
  }

  const loadUsers = async () => {

    try {

      setUserLoading(true)

      const response = await api.get(
        '/api/admin/users',
        getHeaders()
      )

      setUsers(response.data)
      setUserMessage('')

    } catch (error) {

      setUserMessage(
        error.response?.data?.message ||
        'Unable to load users.'
      )

    } finally {

      setUserLoading(false)

    }
  }

  const handleSpecialtyChange = (doctorId, specialty) => {

    setSelectedSpecialties({
      ...selectedSpecialties,
      [doctorId]: specialty
    })
  }

  const updateSpecialty = async (doctorId) => {

    const specialty = selectedSpecialties[doctorId]

    if (!specialty) {
      setMessage('Please select a specialty.')
      return
    }

    try {

      setSavingId(doctorId)
      setMessage('')

      await api.put(
        `/api/doctors/${doctorId}/specialty`,
        {
          specialty: specialty
        },
        getHeaders()
      )

      setDoctors(doctors.map((doctor) => {

        if (doctor.doctorId === doctorId) {
          return {
            ...doctor,
            specialty: specialty
          }
        }

        return doctor
      }))

      setMessage('Doctor specialty updated successfully.')

    } catch (error) {

      setMessage(
        error.response?.data?.message ||
        'Unable to update doctor specialty.'
      )

    } finally {

      setSavingId(null)

    }
  }

  // Activate or deactivate user
  const updateUserStatus = async (user) => {

    const newStatus = !user.enabled

    const action = newStatus ? 'activate' : 'deactivate'

    const confirmed = window.confirm(
      `Are you sure you want to ${action} ${user.fullName}'s account?`
    )

    if (!confirmed) {
      return
    }

    try {

      setUserSavingId(user.userId)
      setUserMessage('')

      const response = await api.patch(
        `/api/admin/users/${user.userId}/status`,
        {
          enabled: newStatus
        },
        getHeaders()
      )

      setUsers(users.map((item) => {

        if (item.userId === user.userId) {
          return response.data
        }

        return item
      }))

      setUserMessage(
        `Account ${newStatus ? 'activated' : 'deactivated'} successfully.`
      )

    } catch (error) {

      setUserMessage(
        error.response?.data?.message ||
        'Unable to update account status.'
      )

    } finally {

      setUserSavingId(null)

    }
  }

  const logout = () => {

    localStorage.removeItem('token')
    localStorage.removeItem('user')

    navigate('/')
  }

  const filteredDoctors = doctors.filter((doctor) => {

    const matchesSearch =
      doctor.fullName.toLowerCase().includes(search.toLowerCase()) ||
      (doctor.clinicName || '').toLowerCase().includes(search.toLowerCase())

    const matchesSpecialty =
      filterSpecialty === '' ||
      doctor.specialty === filterSpecialty

    return matchesSearch && matchesSpecialty
  })

  const filteredUsers = users.filter((user) => {

    const matchesSearch =
      user.fullName.toLowerCase().includes(userSearch.toLowerCase()) ||
      user.email.toLowerCase().includes(userSearch.toLowerCase())

    const matchesRole =
      userRoleFilter === '' ||
      user.role === userRoleFilter

    const matchesStatus =
      userStatusFilter === '' ||
      (userStatusFilter === 'ACTIVE' && user.enabled) ||
      (userStatusFilter === 'INACTIVE' && !user.enabled)

    return matchesSearch && matchesRole && matchesStatus
  })

  const assignedDoctors = doctors.filter(
    (doctor) => doctor.specialty
  ).length

  const unassignedDoctors = doctors.length - assignedDoctors

  const activeUsers = users.filter(
    (user) => user.enabled
  ).length

  const inactiveUsers = users.length - activeUsers

  return (

    <div className="min-vh-100 bg-light">

      {/* Navbar */}

      <nav className="navbar navbar-dark bg-primary shadow-sm px-4">

        <span className="navbar-brand fw-bold fs-4">
          QueueFlow Admin
        </span>

        <button
          className="btn btn-outline-light"
          onClick={logout}
        >
          Logout
        </button>

      </nav>

      <div className="container-fluid p-4">

        {/* Heading */}

        <div className="mb-4">

          <h2 className="fw-bold">
            Admin Dashboard
          </h2>

          <p className="text-muted">
            Manage doctors, medical specialties and registered users.
          </p>

        </div>

        {/* Doctor Statistics */}

        <div className="row g-3 mb-4">

          <div className="col-md-4">

            <div className="card border-0 shadow-sm p-3">

              <p className="text-muted mb-1">
                Total Doctors
              </p>

              <h2 className="fw-bold text-primary">
                {doctors.length}
              </h2>

            </div>

          </div>

          <div className="col-md-4">

            <div className="card border-0 shadow-sm p-3">

              <p className="text-muted mb-1">
                Assigned Specialties
              </p>

              <h2 className="fw-bold text-success">
                {assignedDoctors}
              </h2>

            </div>

          </div>

          <div className="col-md-4">

            <div className="card border-0 shadow-sm p-3">

              <p className="text-muted mb-1">
                Awaiting Assignment
              </p>

              <h2 className="fw-bold text-warning">
                {unassignedDoctors}
              </h2>

            </div>

          </div>

        </div>

        {/* Doctor Message */}

        {message && (

          <div className="alert alert-info">
            {message}
          </div>

        )}

        {/* Doctor Management */}

        <div className="card border-0 shadow-sm mb-4">

          <div className="card-body">

            <div className="d-flex flex-wrap justify-content-between align-items-center mb-4">

              <h4 className="fw-bold mb-3 mb-md-0">
                Doctor Management
              </h4>

              <button
                className="btn btn-outline-primary"
                onClick={loadDashboard}
              >
                Refresh
              </button>

            </div>

            {/* Search and Filter */}

            <div className="row g-3 mb-4">

              <div className="col-md-6">

                <input
                  type="text"
                  className="form-control"
                  placeholder="Search doctor or clinic..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />

              </div>

              <div className="col-md-6">

                <select
                  className="form-select"
                  value={filterSpecialty}
                  onChange={(e) => setFilterSpecialty(e.target.value)}
                >

                  <option value="">
                    All Specialties
                  </option>

                  {specialties.map((specialty) => (

                    <option
                      key={specialty}
                      value={specialty}
                    >
                      {specialty.replaceAll('_', ' ')}
                    </option>

                  ))}

                </select>

              </div>

            </div>

            {/* Doctor List */}

            {loading ? (

              <div className="text-center p-5">
                <div className="spinner-border text-primary"></div>
                <p className="mt-2 text-muted">
                  Loading doctors...
                </p>
              </div>

            ) : filteredDoctors.length === 0 ? (

              <div className="text-center p-5">

                <h5>No doctors found</h5>

                <p className="text-muted">
                  Try changing your search or specialty filter.
                </p>

              </div>

            ) : (

              <div className="table-responsive">

                <table className="table table-hover align-middle">

                  <thead className="table-light">

                    <tr>
                      <th>Doctor</th>
                      <th>Clinic</th>
                      <th>Qualification</th>
                      <th>Experience</th>
                      <th>Consultation Fee</th>
                      <th>Specialty</th>
                      <th>Action</th>
                    </tr>

                  </thead>

                  <tbody>

                    {filteredDoctors.map((doctor) => (

                      <tr key={doctor.doctorId}>

                        <td className="fw-semibold">
                          {doctor.fullName}
                        </td>

                        <td>
                          {doctor.clinicName}
                        </td>

                        <td>
                          {doctor.qualification}
                        </td>

                        <td>
                          {doctor.experienceYears} years
                        </td>

                        <td>
                          ₹{doctor.consultationFee}
                        </td>

                        <td style={{ minWidth: '190px' }}>

                          <select
                            className="form-select form-select-sm"
                            value={selectedSpecialties[doctor.doctorId] || ''}
                            onChange={(e) =>
                              handleSpecialtyChange(
                                doctor.doctorId,
                                e.target.value
                              )
                            }
                          >

                            <option value="">
                              Select Specialty
                            </option>

                            {specialties.map((specialty) => (

                              <option
                                key={specialty}
                                value={specialty}
                              >
                                {specialty.replaceAll('_', ' ')}
                              </option>

                            ))}

                          </select>

                        </td>

                        <td>

                          <button
                            className="btn btn-sm btn-primary"
                            disabled={
                              savingId === doctor.doctorId ||
                              !selectedSpecialties[doctor.doctorId] ||
                              selectedSpecialties[doctor.doctorId] === doctor.specialty
                            }
                            onClick={() => updateSpecialty(doctor.doctorId)}
                          >

                            {savingId === doctor.doctorId
                              ? 'Saving...'
                              : 'Save'}

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

        {/* User Statistics */}

        <div className="row g-3 mb-4">

          <div className="col-md-4">

            <div className="card border-0 shadow-sm p-3">

              <p className="text-muted mb-1">
                Total Registered Users
              </p>

              <h2 className="fw-bold text-primary">
                {users.length}
              </h2>

            </div>

          </div>

          <div className="col-md-4">

            <div className="card border-0 shadow-sm p-3">

              <p className="text-muted mb-1">
                Active Accounts
              </p>

              <h2 className="fw-bold text-success">
                {activeUsers}
              </h2>

            </div>

          </div>

          <div className="col-md-4">

            <div className="card border-0 shadow-sm p-3">

              <p className="text-muted mb-1">
                Inactive Accounts
              </p>

              <h2 className="fw-bold text-danger">
                {inactiveUsers}
              </h2>

            </div>

          </div>

        </div>

        {/* User Management */}

        <div className="card border-0 shadow-sm">

          <div className="card-body">

            <div className="d-flex flex-wrap justify-content-between align-items-center mb-4">

              <div>
                <h4 className="fw-bold mb-1">
                  User Management
                </h4>

                <p className="text-muted mb-0">
                  View and manage registered accounts.
                </p>
              </div>

              <button
                className="btn btn-outline-primary"
                onClick={loadUsers}
                disabled={userLoading}
              >
                {userLoading ? 'Loading...' : 'Refresh Users'}
              </button>

            </div>

            {userMessage && (

              <div className="alert alert-info">
                {userMessage}
              </div>

            )}

            {/* User Search and Filters */}

            <div className="row g-3 mb-4">

              <div className="col-md-4">

                <input
                  type="text"
                  className="form-control"
                  placeholder="Search name or email..."
                  value={userSearch}
                  onChange={(e) => setUserSearch(e.target.value)}
                />

              </div>

              <div className="col-md-4">

                <select
                  className="form-select"
                  value={userRoleFilter}
                  onChange={(e) => setUserRoleFilter(e.target.value)}
                >

                  <option value="">
                    All Roles
                  </option>

                  <option value="ADMIN">
                    Admin
                  </option>

                  <option value="DOCTOR">
                    Doctor
                  </option>

                  <option value="PATIENT">
                    Patient
                  </option>

                </select>

              </div>

              <div className="col-md-4">

                <select
                  className="form-select"
                  value={userStatusFilter}
                  onChange={(e) => setUserStatusFilter(e.target.value)}
                >

                  <option value="">
                    All Statuses
                  </option>

                  <option value="ACTIVE">
                    Active
                  </option>

                  <option value="INACTIVE">
                    Inactive
                  </option>

                </select>

              </div>

            </div>

            {/* User Table */}

            {userLoading ? (

              <div className="text-center p-5">

                <div className="spinner-border text-primary"></div>

                <p className="mt-2 text-muted">
                  Loading users...
                </p>

              </div>

            ) : filteredUsers.length === 0 ? (

              <div className="text-center p-5">

                <h5>No users found</h5>

                <p className="text-muted">
                  Try changing your search or filters.
                </p>

              </div>

            ) : (

              <div className="table-responsive">

                <table className="table table-hover align-middle">

                  <thead className="table-light">

                    <tr>
                      <th>User ID</th>
                      <th>Full Name</th>
                      <th>Email</th>
                      <th>Phone</th>
                      <th>Role</th>
                      <th>Status</th>
                      <th>Registered On</th>
                      <th>Action</th>
                    </tr>

                  </thead>

                  <tbody>

                    {filteredUsers.map((user) => (

                      <tr key={user.userId}>

                        <td>
                          {user.userId}
                        </td>

                        <td className="fw-semibold">
                          {user.fullName}
                        </td>

                        <td>
                          {user.email}
                        </td>

                        <td>
                          {user.phone || 'Not provided'}
                        </td>

                        <td>

                          <span className={`badge ${
                            user.role === 'ADMIN'
                              ? 'bg-primary'
                              : user.role === 'DOCTOR'
                              ? 'bg-info text-dark'
                              : 'bg-secondary'
                          }`}>
                            {user.role}
                          </span>

                        </td>

                        <td>

                          <span className={`badge ${
                            user.enabled
                              ? 'bg-success'
                              : 'bg-danger'
                          }`}>
                            {user.enabled ? 'Active' : 'Inactive'}
                          </span>

                        </td>

                        <td>
                          {user.createdAt
                            ? new Date(user.createdAt).toLocaleDateString()
                            : 'N/A'}
                        </td>

                        <td>

                          <button
                            className={`btn btn-sm ${
                              user.enabled
                                ? 'btn-outline-danger'
                                : 'btn-outline-success'
                            }`}
                            disabled={
                              userSavingId === user.userId ||
                              user.role === 'ADMIN'
                            }
                            onClick={() => updateUserStatus(user)}
                          >

                            {userSavingId === user.userId
                              ? 'Updating...'
                              : user.enabled
                              ? 'Deactivate'
                              : 'Activate'}

                          </button>

                          {user.role === 'ADMIN' && (
                            <small className="d-block text-muted mt-1">
                              Protected account
                            </small>
                          )}

                        </td>

                      </tr>

                    ))}

                  </tbody>

                </table>

              </div>

            )}

          </div>

        </div>

      </div>

    </div>

  )
}

export default AdminDashboard
