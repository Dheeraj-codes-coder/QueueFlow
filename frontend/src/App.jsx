import { Routes, Route } from 'react-router-dom'

import Login from './pages/Login'
import AdminDashboard from './pages/AdminDashboard'

import DoctorDashboard from './pages/DoctorDashboard'
import PatientDashboard from './pages/PatientDashboard'

import DoctorAppointments from './pages/DoctorAppointments'
import DoctorUpcomingAppointments from './pages/DoctorUpcomingAppointments'
import DoctorQueue from './pages/DoctorQueue'

import PatientAppointments from './pages/PatientAppointments'
import BookAppointment from './pages/BookAppointment'

function App() {

  return (
    <Routes>

      <Route path="/" element={<Login />} />

      {/* Admin */}
      <Route path="/admin-dashboard" element={<AdminDashboard />} />

      {/* Doctor */}
      <Route path="/doctor-dashboard" element={<DoctorDashboard />} />
      <Route path="/doctor-appointments" element={<DoctorAppointments />} />
      <Route path="/doctor-upcoming-appointments" element={<DoctorUpcomingAppointments />} />
      <Route path="/doctor-queue" element={<DoctorQueue />} />

      {/* Patient */}
      <Route path="/patient-dashboard" element={<PatientDashboard />} />
      <Route path="/patient-appointments" element={<PatientAppointments />} />
      <Route path="/book-appointment" element={<BookAppointment />} />

    </Routes>
  )
}

export default App