
import { useEffect, useState } from 'react'
import api from '../services/api'

function PatientPrescriptions() {
  const user = JSON.parse(localStorage.getItem('user'))
  const token = localStorage.getItem('token')

  const headers = {
    Authorization: `Bearer ${token}`
  }

  const [prescriptions, setPrescriptions] = useState([])
  const [selectedPrescription, setSelectedPrescription] = useState(null)
  const [patient, setPatient] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadPrescriptions()
  }, [])

  const loadPrescriptions = async () => {
    try {
      const dashboardResponse = await api.get(
        '/api/patients/me/dashboard',
        { headers }
      )

      setPatient(dashboardResponse.data)

      const response = await api.get(
        `/api/prescriptions/patients/${dashboardResponse.data.patientId}/history`,
        { headers }
      )

      setPrescriptions(response.data)
    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Unable to load prescriptions.'
      )
    } finally {
      setLoading(false)
    }
  }

  const openPrescription = (prescription) => {
    setSelectedPrescription(prescription)
    setError('')
  }

  const downloadPrescription = () => {
    window.print()
  }

  return (
    <div className="container mt-4 mb-5">

      <button
        className="btn btn-outline-secondary mb-3 no-print"
        onClick={() => {
          window.location.href = '/patient-dashboard'
        }}
      >
        Back to Dashboard
      </button>

      <div className="card shadow p-4">

        <h3 className="no-print">My Prescriptions</h3>
        <p className="no-print">
          View your prescriptions and prescribed medicines.
        </p>

        {loading && (
          <p>Loading prescriptions...</p>
        )}

        {error && (
          <div className="alert alert-danger no-print">
            {error}
          </div>
        )}

        {!loading && !selectedPrescription && (
          <>
            {prescriptions.length === 0 ? (
              <p>No prescriptions found.</p>
            ) : (
              <div className="table-responsive">
                <table className="table table-bordered table-striped">
                  <thead>
                    <tr>
                      <th>Prescription ID</th>
                      <th>Consultation Date</th>
                      <th>Prescribed At</th>
                      <th>Medicines</th>
                      <th>Action</th>
                    </tr>
                  </thead>

                  <tbody>
                    {prescriptions.map(item => (
                      <tr key={item.prescriptionId}>
                        <td>{item.prescriptionId}</td>
                        <td>
                          {item.consultationStartedAt
                            ? item.consultationStartedAt.split('T')[0]
                            : 'N/A'}
                        </td>
                        <td>
                          {item.prescribedAt
                            ? item.prescribedAt.split('T')[0]
                            : 'N/A'}
                        </td>
                        <td>{item.medicines?.length || 0}</td>
                        <td>
                          <button
                            className="btn btn-primary btn-sm"
                            onClick={() => openPrescription(item)}
                          >
                            View Prescription
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}

        {selectedPrescription && (
          <div className="prescription-print">

            <div className="d-flex justify-content-between align-items-center mb-3 no-print">
              <button
                className="btn btn-outline-secondary"
                onClick={() => setSelectedPrescription(null)}
              >
                Back to Prescriptions
              </button>

              <button
                className="btn btn-success"
                onClick={downloadPrescription}
              >
                Download PDF
              </button>
            </div>

            <div className="text-center border-bottom pb-3 mb-3">
              <h2>QueueFlow</h2>
              <h5>Medical Prescription</h5>
              <p className="mb-0">
                Prescription ID: {selectedPrescription.prescriptionId}
              </p>
            </div>

            <h5>Patient Details</h5>

            <p>
              <strong>Name:</strong> {patient?.patientName || user?.fullName}
            </p>

            <p>
              <strong>Email:</strong> {patient?.patientEmail || user?.email}
            </p>

            <p>
              <strong>Consultation Date:</strong>{' '}
              {selectedPrescription.consultationStartedAt
                ? selectedPrescription.consultationStartedAt.split('T')[0]
                : 'N/A'}
            </p>

            <p>
              <strong>Prescription Date:</strong>{' '}
              {selectedPrescription.prescribedAt
                ? selectedPrescription.prescribedAt.split('T')[0]
                : 'N/A'}
            </p>

            <hr />

            <h5>Prescription Instructions</h5>

            <p>
              {selectedPrescription.instructions || 'No instructions provided.'}
            </p>

            <hr />

            <h5>Prescribed Medicines</h5>

            {selectedPrescription.medicines?.length > 0 ? (
              <div className="table-responsive">
                <table className="table table-bordered">
                  <thead>
                    <tr>
                      <th>Medicine</th>
                      <th>Dosage</th>
                      <th>Frequency</th>
                      <th>Duration</th>
                      <th>Quantity</th>
                      <th>Instructions</th>
                    </tr>
                  </thead>

                  <tbody>
                    {selectedPrescription.medicines.map(medicine => (
                      <tr key={medicine.prescriptionItemId}>
                        <td>{medicine.medicineName}</td>
                        <td>{medicine.dosage}</td>
                        <td>{medicine.frequency}</td>
                        <td>{medicine.durationDays} days</td>
                        <td>{medicine.quantity}</td>
                        <td>{medicine.instructions}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p>No medicines prescribed.</p>
            )}

            <hr />

            <p className="text-muted">
              This prescription was generated through QueueFlow.
            </p>

          </div>
        )}

      </div>

      <style>
        {`
          @media print {
            body * {
              visibility: hidden;
            }

            .prescription-print,
            .prescription-print * {
              visibility: visible;
            }

            .prescription-print {
              position: absolute;
              left: 0;
              top: 0;
              width: 100%;
              padding: 20px;
            }

            .no-print {
              display: none !important;
            }

            table {
              width: 100%;
              border-collapse: collapse;
            }

            th, td {
              border: 1px solid #000 !important;
              padding: 8px !important;
            }
          }
        `}
      </style>

    </div>
  )
}

export default PatientPrescriptions
