
function DoctorDashboard() {
  const user = JSON.parse(localStorage.getItem('user'))

  return (
    <div className="container mt-4">
      <div className="card shadow p-4">

        <h2>QueueFlow</h2>

        <h4 className="mt-3">Doctor Dashboard</h4>

        <p>Welcome, {user?.fullName}</p>
        <p>Manage your clinic activities from here.</p>

        <div className="row mt-4">

          {/* Appointments */}
          <div className="col-md-4 mb-3">
            <div className="card p-3 h-100">

              <h5>Appointments</h5>
              <p>View patient appointments.</p>

              <button
                className="btn btn-primary mb-2"
                onClick={() => window.location.href = '/doctor-appointments'}
              >
                Today's Appointments
              </button>

              <button
                className="btn btn-outline-primary"
                onClick={() => window.location.href = '/doctor-upcoming-appointments'}
              >
                Upcoming Appointments
              </button>

            </div>
          </div>

          {/* Queue Management */}
          <div className="col-md-4 mb-3">
            <div className="card p-3 h-100">

              <h5>Queue Management</h5>
              <p>Manage your patient queue.</p>

              <button
                className="btn btn-primary"
                onClick={() => window.location.href = '/doctor-queue'}
              >
                Manage Queue
              </button>

            </div>
          </div>

          {/* Prescriptions */}
          <div className="col-md-4 mb-3">
            <div className="card p-3 h-100">
              <h5>Prescriptions</h5>
              <p>Manage patient prescriptions.</p>
            </div>
          </div>

        </div>

        <button
          className="btn btn-danger mt-3"
          onClick={() => {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.href = '/'
          }}
        >
          Logout
        </button>

      </div>
    </div>
  )
}

export default DoctorDashboard
