import { useState } from 'react'
import api from '../services/api'

function Login() {

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()

    try {

      const response = await api.post('/api/auth/login', {
        email: email,
        password: password
      })

      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data))

      setMessage('Login successful!')

      if (response.data.role === 'ADMIN') {
        window.location.href = '/admin-dashboard'
      }
      else if (response.data.role === 'DOCTOR') {
        window.location.href = '/doctor-dashboard'
      }
      else if (response.data.role === 'PATIENT') {
        window.location.href = '/patient-dashboard'
      }

    } catch (error) {

      setMessage(
        error.response?.data?.message || 'Login failed. Please try again.'
      )

    }
  }

  return (
    <div className="container d-flex justify-content-center align-items-center min-vh-100">

      <div className="card shadow p-4" style={{ width: '400px' }}>

        <h2 className="text-center mb-2">QueueFlow</h2>

        <p className="text-center text-muted mb-4">
          Clinic Management System
        </p>

        <h4 className="mb-3">Welcome Back</h4>

        <form onSubmit={handleLogin}>

          <div className="mb-3">
            <label className="form-label">Email Address</label>

            <input
              type="email"
              className="form-control"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Password</label>

            <input
              type="password"
              className="form-control"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-primary w-100">
            Login
          </button>

        </form>

        {message && (
          <p className="text-center mt-3">{message}</p>
        )}

      </div>

    </div>
  )
}

export default Login