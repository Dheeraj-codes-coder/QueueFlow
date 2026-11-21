
import { useState } from 'react'
import api from '../services/api'
import './Login.css'

function Login() {

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const handleLogin = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

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

    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">

      <div className="login-left">

        <div className="login-brand">
          <div className="brand-icon">+</div>
          <span>QueueFlow</span>
        </div>

        <div className="login-hero">
          <span className="hero-label">HEALTHCARE MANAGEMENT</span>

          <h1>
            Better care.
            <br />
            <span>Better connections.</span>
          </h1>

          <p>
            Manage appointments, coordinate patient queues
            and simplify clinic operations with QueueFlow.
          </p>

          <div className="hero-features">
            <div>✓ Easy appointment management</div>
            <div>✓ Organized patient queues</div>
            <div>✓ Digital prescriptions</div>
          </div>
        </div>

        <div className="login-footer">
          © 2026 QueueFlow. All rights reserved.
        </div>

      </div>

      <div className="login-right">

        <div className="login-form-container">

          <div className="mobile-brand">
            <div className="brand-icon">+</div>
            <span>QueueFlow</span>
          </div>

          <span className="form-label">WELCOME BACK</span>

          <h2>Sign in to your account</h2>

          <p className="form-description">
            Enter your details to continue to QueueFlow.
          </p>

          <form onSubmit={handleLogin}>

            <div className="login-field">
              <label>Email Address</label>

              <input
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="login-field">
              <label>Password</label>

              <input
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="login-submit"
              disabled={loading}
            >
              {loading ? 'Signing in...' : 'Sign In'}
              <span>→</span>
            </button>

          </form>

          {message && (
            <div className="login-message">
              {message}
            </div>
          )}

          <div className="login-security">
            <span>🔒</span>
            Your account is protected with secure authentication.
          </div>

        </div>

      </div>

    </div>
  )
}

export default Login
