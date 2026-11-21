
function DoctorDashboard() {

  const user = JSON.parse(localStorage.getItem('user'))

  const openPage = (path) => {
    window.location.href = path
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/'
  }

  return (
    <div className="dashboard-page">

      <aside className="dashboard-sidebar">

        <div className="dashboard-brand">
          <div className="dashboard-brand-icon">+</div>
          <span>QueueFlow</span>
        </div>

        <p className="sidebar-label">DOCTOR MENU</p>

        <button
          className="sidebar-link active"
          onClick={() => openPage('/doctor-dashboard')}
        >
          <span>▦</span> Dashboard
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/doctor-appointments')}
        >
          <span>▣</span> Today's Appointments
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/doctor-upcoming-appointments')}
        >
          <span>◷</span> Upcoming Appointments
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/doctor-queue')}
        >
          <span>☷</span> Queue Management
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/doctor-schedule')}
        >
          <span>▤</span> Schedule Management
        </button>

        <button
          className="sidebar-link"
          onClick={() => openPage('/doctor-prescriptions')}
        >
          <span>✚</span> Prescriptions
        </button>

        <div className="sidebar-bottom">
          <button className="sidebar-logout" onClick={logout}>
            ↪ Logout
          </button>
        </div>

      </aside>

      <main className="dashboard-main">

        <header className="dashboard-header">

          <div>
            <h2>Doctor Dashboard</h2>
            <p>Manage your clinic activities from one place.</p>
          </div>

          <div className="dashboard-user">
            <div className="user-avatar">
              {user?.fullName?.charAt(0)?.toUpperCase() || 'D'}
            </div>

            <div>
              <strong>{user?.fullName}</strong>
              <span>Doctor</span>
            </div>
          </div>

        </header>

        <section className="dashboard-welcome">

          <div>
            <span>DOCTOR WORKSPACE</span>

            <h1>
              Welcome, {user?.fullName || 'Doctor'}!
            </h1>

            <p>
              Coordinate appointments, manage your patient queue
              and maintain your consultation schedule.
            </p>
          </div>

          <div className="welcome-symbol">✚</div>

        </section>

        <section className="dashboard-section">

          <div className="section-heading">
            <h3>Clinic Management</h3>
            <p>Choose a section to manage your clinic</p>
          </div>

          <div className="dashboard-actions">

            <div className="action-card">
              <div className="action-icon blue">▣</div>

              <h4>Today's Appointments</h4>

              <p>
                View today's appointments and patient details.
              </p>

              <button onClick={() => openPage('/doctor-appointments')}>
                View Appointments <span>→</span>
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon green">◷</div>

              <h4>Upcoming Appointments</h4>

              <p>
                Review your upcoming patient appointments.
              </p>

              <button onClick={() => openPage('/doctor-upcoming-appointments')}>
                View Upcoming <span>→</span>
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon purple">☷</div>

              <h4>Queue Management</h4>

              <p>
                Manage patient check-ins and consultation queues.
              </p>

              <button onClick={() => openPage('/doctor-queue')}>
                Manage Queue <span>→</span>
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon orange">▤</div>

              <h4>Schedule Management</h4>

              <p>
                Manage your weekly working days and consultation timings.
              </p>

              <button onClick={() => openPage('/doctor-schedule')}>
                Manage Schedule <span>→</span>
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon blue">✚</div>

              <h4>Prescriptions</h4>

              <p>
                Create prescriptions and manage patient medicines.
              </p>

              <button onClick={() => openPage('/doctor-prescriptions')}>
                Manage Prescriptions <span>→</span>
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

export default DoctorDashboard
