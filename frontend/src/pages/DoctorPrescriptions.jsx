
import { useCallback, useEffect, useState } from 'react'
import api from '../services/api'

function DoctorPrescriptions() {

  const user = JSON.parse(localStorage.getItem('user'))
  const token = localStorage.getItem('token')

  const headers = {
    Authorization: `Bearer ${token}`
  }

  const [consultations, setConsultations] = useState([])
  const [selectedConsultation, setSelectedConsultation] = useState(null)
  const [prescriptionId, setPrescriptionId] = useState(null)
  const [prescription, setPrescription] = useState(null)
  const [medicines, setMedicines] = useState([])

  const [instructions, setInstructions] = useState('')

  const [medicineName, setMedicineName] = useState('')
  const [dosage, setDosage] = useState('')
  const [frequency, setFrequency] = useState('')
  const [durationDays, setDurationDays] = useState(1)
  const [quantity, setQuantity] = useState(1)
  const [medicineInstructions, setMedicineInstructions] = useState('')

  const [editingMedicineId, setEditingMedicineId] = useState(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const loadConsultations = useCallback(async () => {
    try {

      setLoading(true)

      const doctorResponse = await api.get('/api/doctors', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })

      const doctor = doctorResponse.data.find(
        item =>
          item.fullName?.trim().toLowerCase() ===
          user?.fullName?.trim().toLowerCase()
      )

      if (!doctor) {
        setError('Doctor profile not found.')
        return
      }

      const response = await api.get(
        `/api/doctors/${doctor.doctorId}/consultations/history`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      )

      setConsultations(response.data)
      setError('')

    } catch (err) {

      setError(
        err.response?.data?.message ||
        'Unable to load consultation history.'
      )

    } finally {
      setLoading(false)
    }
  }, [token, user?.fullName])

  useEffect(() => {
    loadConsultations()
  }, [loadConsultations])

  const loadPrescription = async (id) => {
    try {

      const response = await api.get(
        `/api/prescriptions/${id}`,
        { headers }
      )

      setPrescription(response.data)
      setPrescriptionId(response.data.prescriptionId)
      setInstructions(response.data.instructions || '')
      setMedicines(response.data.medicines || [])

    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Unable to load prescription.'
      )
    }
  }

  const openConsultation = async (consultation) => {

    setSelectedConsultation(consultation)
    setPrescription(null)
    setMedicines([])
    setInstructions('')
    setPrescriptionId(consultation.prescriptionId)
    setMessage('')
    setError('')

    if (consultation.prescriptionId) {
      await loadPrescription(consultation.prescriptionId)
    }
  }

  const createPrescription = async () => {
    try {

      const response = await api.post(
        '/api/prescriptions/create',
        {
          consultationId: selectedConsultation.consultationId,
          instructions
        },
        { headers }
      )

      const newPrescriptionId = response.data.prescriptionId

      setPrescriptionId(newPrescriptionId)

      setConsultations(previous =>
        previous.map(item =>
          item.consultationId === selectedConsultation.consultationId
            ? { ...item, prescriptionId: newPrescriptionId }
            : item
        )
      )

      setMessage('Prescription created successfully.')

      await loadPrescription(newPrescriptionId)

    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Unable to create prescription.'
      )
    }
  }

  const saveMedicine = async (e) => {
    e.preventDefault()

    const medicineData = {
      medicineName,
      dosage,
      frequency,
      durationDays: Number(durationDays),
      quantity: Number(quantity),
      instructions: medicineInstructions
    }

    try {

      setError('')
      setMessage('')

      if (editingMedicineId) {

        await api.put(
          `/api/prescriptions/${prescriptionId}/medicines/${editingMedicineId}`,
          medicineData,
          { headers }
        )

        setMessage('Medicine updated successfully.')

      } else {

        await api.post(
          `/api/prescriptions/${prescriptionId}/medicines`,
          medicineData,
          { headers }
        )

        setMessage('Medicine added successfully.')
      }

      clearMedicineForm()
      await loadPrescription(prescriptionId)

    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Unable to save medicine.'
      )
    }
  }

  const editMedicine = (medicine) => {

    setEditingMedicineId(medicine.prescriptionItemId)
    setMedicineName(medicine.medicineName)
    setDosage(medicine.dosage)
    setFrequency(medicine.frequency)
    setDurationDays(medicine.durationDays)
    setQuantity(medicine.quantity)
    setMedicineInstructions(medicine.instructions || '')

    setMessage('')
    setError('')
  }

  const deleteMedicine = async (medicineId) => {

    if (!window.confirm('Are you sure you want to delete this medicine?')) {
      return
    }

    try {

      await api.delete(
        `/api/prescriptions/${prescriptionId}/medicines/${medicineId}`,
        { headers }
      )

      setMessage('Medicine deleted successfully.')
      setError('')

      await loadPrescription(prescriptionId)

    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Unable to delete medicine.'
      )
    }
  }

  const clearMedicineForm = () => {

    setEditingMedicineId(null)
    setMedicineName('')
    setDosage('')
    setFrequency('')
    setDurationDays(1)
    setQuantity(1)
    setMedicineInstructions('')
  }

  const closePrescription = () => {

    setSelectedConsultation(null)
    setPrescription(null)
    setPrescriptionId(null)
    setMedicines([])
    setMessage('')
    setError('')

    clearMedicineForm()
  }

  const getStatusStyle = (status) => {

    const value = status?.toUpperCase()

    if (value === 'COMPLETED') {
      return {
        background: '#dcfce7',
        color: '#15803d'
      }
    }

    if (value === 'CANCELLED') {
      return {
        background: '#fee2e2',
        color: '#b91c1c'
      }
    }

    return {
      background: '#dbeafe',
      color: '#1d4ed8'
    }
  }

  const cardStyle = {
    background: '#ffffff',
    border: '1px solid #e8edf3',
    borderRadius: '14px',
    boxShadow: '0 3px 12px rgba(15, 23, 42, 0.04)'
  }

  const inputStyle = {
    borderRadius: '9px',
    padding: '10px 12px'
  }

  if (loading) {
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ minHeight: '70vh', background: '#f5f7fb' }}
      >
        <div className="text-center">
          <div className="spinner-border text-primary mb-3"></div>
          <p className="text-secondary">Loading consultations...</p>
        </div>
      </div>
    )
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        background: '#f5f7fb',
        padding: '30px 15px'
      }}
    >

      <div className="container" style={{ maxWidth: '1200px' }}>

        {/* HEADER */}

        <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">

          <div>
            <p
              className="mb-1"
              style={{
                color: '#2563eb',
                fontSize: '13px',
                fontWeight: '600',
                letterSpacing: '1px'
              }}
            >
              DOCTOR PORTAL
            </p>

            <h2 className="fw-bold mb-1" style={{ color: '#172033' }}>
              Prescription Management
            </h2>

            <p className="text-secondary mb-0">
              Manage patient consultations and prescribed medicines.
            </p>
          </div>

          <div className="d-flex gap-2">

            <button
              className="btn btn-outline-secondary"
              style={{ borderRadius: '9px' }}
              onClick={() => window.location.href = '/doctor-dashboard'}
            >
              ← Dashboard
            </button>

            <button
              className="btn btn-primary"
              style={{ borderRadius: '9px' }}
              onClick={loadConsultations}
            >
              ↻ Refresh
            </button>

          </div>
        </div>

        {/* ALERTS */}

        {message && (
          <div className="alert alert-success border-0 shadow-sm">
            {message}
          </div>
        )}

        {error && (
          <div className="alert alert-danger border-0 shadow-sm">
            {error}
          </div>
        )}

        {/* CONSULTATION HISTORY */}

        {!selectedConsultation && (
          <div className="p-4" style={cardStyle}>

            <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">

              <div>
                <h5 className="fw-bold mb-1">
                  Consultation History
                </h5>

                <p className="text-secondary mb-0 small">
                  Select a consultation to view or create a prescription.
                </p>
              </div>

              <span
                className="badge rounded-pill px-3 py-2"
                style={{
                  background: '#eff6ff',
                  color: '#2563eb'
                }}
              >
                {consultations.length} Consultations
              </span>

            </div>

            {consultations.length === 0 ? (

              <div className="text-center py-5">

                <div style={{ fontSize: '42px' }}>📋</div>

                <h5 className="fw-bold mt-3">
                  No Consultation History
                </h5>

                <p className="text-secondary mb-0">
                  Completed consultations will appear here.
                </p>

              </div>

            ) : (

              <div className="table-responsive">

                <table className="table align-middle">

                  <thead>
                    <tr style={{ background: '#f8fafc' }}>
                      <th className="py-3">Patient</th>
                      <th>Date</th>
                      <th>Reason</th>
                      <th>Status</th>
                      <th>Prescription</th>
                      <th>Action</th>
                    </tr>
                  </thead>

                  <tbody>

                    {consultations.map(item => (

                      <tr key={item.consultationId}>

                        <td className="py-3">
                          <div className="fw-semibold">
                            {item.patientName}
                          </div>

                          <small className="text-secondary">
                            Consultation #{item.consultationId}
                          </small>
                        </td>

                        <td>{item.appointmentDate}</td>

                        <td style={{ minWidth: '150px' }}>
                          {item.appointmentReason || 'Not specified'}
                        </td>

                        <td>
                          <span
                            className="badge rounded-pill px-3 py-2"
                            style={getStatusStyle(item.appointmentStatus)}
                          >
                            {item.appointmentStatus}
                          </span>
                        </td>

                        <td>
                          {item.prescriptionId ? (
                            <span className="text-success fw-semibold">
                              ✓ Created
                            </span>
                          ) : (
                            <span className="text-secondary">
                              Not created
                            </span>
                          )}
                        </td>

                        <td>
                          <button
                            className="btn btn-sm btn-primary"
                            style={{ borderRadius: '8px' }}
                            onClick={() => openConsultation(item)}
                          >
                            {item.prescriptionId
                              ? 'View Prescription'
                              : 'Create Prescription'}
                          </button>
                        </td>

                      </tr>

                    ))}

                  </tbody>

                </table>

              </div>
            )}

          </div>
        )}

        {/* SELECTED CONSULTATION */}

        {selectedConsultation && (
          <div>

            <button
              className="btn btn-outline-secondary mb-3"
              style={{ borderRadius: '9px' }}
              onClick={closePrescription}
            >
              ← Back to Consultation History
            </button>

            {/* PATIENT DETAILS */}

            <div className="p-4 mb-4" style={cardStyle}>

              <div className="d-flex justify-content-between align-items-center mb-3">

                <div>
                  <h5 className="fw-bold mb-1">
                    Patient Details
                  </h5>

                  <p className="text-secondary small mb-0">
                    Consultation #{selectedConsultation.consultationId}
                  </p>
                </div>

                <span
                  className="badge rounded-pill px-3 py-2"
                  style={getStatusStyle(selectedConsultation.appointmentStatus)}
                >
                  {selectedConsultation.appointmentStatus}
                </span>

              </div>

              <div className="row g-3">

                <div className="col-md-6">
                  <small className="text-secondary">Patient Name</small>
                  <div className="fw-semibold">
                    {selectedConsultation.patientName}
                  </div>
                </div>

                <div className="col-md-6">
                  <small className="text-secondary">Email</small>
                  <div className="fw-semibold">
                    {selectedConsultation.patientEmail}
                  </div>
                </div>

                <div className="col-md-6">
                  <small className="text-secondary">Phone</small>
                  <div className="fw-semibold">
                    {selectedConsultation.patientPhone}
                  </div>
                </div>

                <div className="col-md-6">
                  <small className="text-secondary">Appointment Date</small>
                  <div className="fw-semibold">
                    {selectedConsultation.appointmentDate}
                  </div>
                </div>

                <div className="col-md-6">
                  <small className="text-secondary">Reason</small>
                  <div className="fw-semibold">
                    {selectedConsultation.appointmentReason || 'Not specified'}
                  </div>
                </div>

                <div className="col-md-6">
                  <small className="text-secondary">Consultation Notes</small>
                  <div className="fw-semibold">
                    {selectedConsultation.consultationNotes || 'No notes'}
                  </div>
                </div>

              </div>

            </div>

            {/* CREATE PRESCRIPTION */}

            {!prescriptionId && (
              <div className="p-4 mb-4" style={cardStyle}>

                <h5 className="fw-bold mb-3">
                  Create Prescription
                </h5>

                {selectedConsultation.consultationEndedAt ? (

                  <>
                    <label className="form-label fw-semibold">
                      Prescription Instructions
                    </label>

                    <textarea
                      className="form-control mb-3"
                      style={inputStyle}
                      rows="4"
                      value={instructions}
                      onChange={e => setInstructions(e.target.value)}
                      placeholder="Enter prescription instructions..."
                    />

                    <button
                      className="btn btn-success px-4"
                      style={{ borderRadius: '9px' }}
                      onClick={createPrescription}
                    >
                      Create Prescription
                    </button>
                  </>

                ) : (

                  <div className="alert alert-warning mb-0">
                    Prescription can only be created after consultation is completed.
                  </div>

                )}

              </div>
            )}

            {/* PRESCRIPTION DETAILS */}

            {prescriptionId && prescription && (
              <>

                <div className="p-4 mb-4" style={cardStyle}>

                  <h5 className="fw-bold mb-3">
                    Prescription Details
                  </h5>

                  <div className="row g-3">

                    <div className="col-md-4">
                      <small className="text-secondary">Prescription ID</small>
                      <div className="fw-semibold">
                        #{prescription.prescriptionId}
                      </div>
                    </div>

                    <div className="col-md-4">
                      <small className="text-secondary">Created At</small>
                      <div className="fw-semibold">
                        {prescription.prescribedAt}
                      </div>
                    </div>

                    <div className="col-md-4">
                      <small className="text-secondary">Medicines</small>
                      <div className="fw-semibold">
                        {medicines.length}
                      </div>
                    </div>

                  </div>

                  <hr />

                  <small className="text-secondary">
                    Prescription Instructions
                  </small>

                  <p className="mb-0 mt-1">
                    {prescription.instructions || 'No instructions'}
                  </p>

                </div>

                {/* MEDICINE FORM */}

                <div className="p-4 mb-4" style={cardStyle}>

                  <div className="d-flex justify-content-between align-items-center mb-4">

                    <div>
                      <h5 className="fw-bold mb-1">
                        {editingMedicineId ? 'Edit Medicine' : 'Add Medicine'}
                      </h5>

                      <p className="text-secondary small mb-0">
                        Enter the medicine details below.
                      </p>
                    </div>

                    {editingMedicineId && (
                      <span className="badge bg-warning text-dark">
                        Editing
                      </span>
                    )}

                  </div>

                  <form onSubmit={saveMedicine}>

                    <div className="row g-3">

                      <div className="col-md-6">
                        <label className="form-label fw-semibold">
                          Medicine Name
                        </label>

                        <input
                          className="form-control"
                          style={inputStyle}
                          value={medicineName}
                          onChange={e => setMedicineName(e.target.value)}
                          placeholder="Enter medicine name"
                          required
                        />
                      </div>

                      <div className="col-md-6">
                        <label className="form-label fw-semibold">
                          Dosage
                        </label>

                        <input
                          className="form-control"
                          style={inputStyle}
                          value={dosage}
                          onChange={e => setDosage(e.target.value)}
                          placeholder="Example: 500 mg"
                          required
                        />
                      </div>

                      <div className="col-md-6">
                        <label className="form-label fw-semibold">
                          Frequency
                        </label>

                        <input
                          className="form-control"
                          style={inputStyle}
                          value={frequency}
                          onChange={e => setFrequency(e.target.value)}
                          placeholder="Example: Twice daily"
                          required
                        />
                      </div>

                      <div className="col-md-3">
                        <label className="form-label fw-semibold">
                          Duration (Days)
                        </label>

                        <input
                          type="number"
                          min="1"
                          className="form-control"
                          style={inputStyle}
                          value={durationDays}
                          onChange={e => setDurationDays(e.target.value)}
                          required
                        />
                      </div>

                      <div className="col-md-3">
                        <label className="form-label fw-semibold">
                          Quantity
                        </label>

                        <input
                          type="number"
                          min="1"
                          className="form-control"
                          style={inputStyle}
                          value={quantity}
                          onChange={e => setQuantity(e.target.value)}
                          required
                        />
                      </div>

                      <div className="col-12">
                        <label className="form-label fw-semibold">
                          Medicine Instructions
                        </label>

                        <textarea
                          className="form-control"
                          style={inputStyle}
                          rows="2"
                          value={medicineInstructions}
                          onChange={e => setMedicineInstructions(e.target.value)}
                          placeholder="Example: Take after food"
                        />
                      </div>

                    </div>

                    <div className="mt-4">

                      <button
                        className="btn btn-primary px-4 me-2"
                        type="submit"
                        style={{ borderRadius: '9px' }}
                      >
                        {editingMedicineId ? 'Update Medicine' : '+ Add Medicine'}
                      </button>

                      {editingMedicineId && (
                        <button
                          type="button"
                          className="btn btn-outline-secondary"
                          style={{ borderRadius: '9px' }}
                          onClick={clearMedicineForm}
                        >
                          Cancel Edit
                        </button>
                      )}

                    </div>

                  </form>

                </div>

                {/* MEDICINES LIST */}

                <div className="p-4" style={cardStyle}>

                  <div className="d-flex justify-content-between align-items-center mb-4">

                    <div>
                      <h5 className="fw-bold mb-1">
                        Prescribed Medicines
                      </h5>

                      <p className="text-secondary small mb-0">
                        Medicines added to this prescription.
                      </p>
                    </div>

                    <span
                      className="badge rounded-pill px-3 py-2"
                      style={{
                        background: '#eff6ff',
                        color: '#2563eb'
                      }}
                    >
                      {medicines.length} Medicines
                    </span>

                  </div>

                  {medicines.length === 0 ? (

                    <div className="text-center py-4">

                      <div style={{ fontSize: '38px' }}>💊</div>

                      <h6 className="fw-bold mt-3">
                        No Medicines Added
                      </h6>

                      <p className="text-secondary mb-0">
                        Use the form above to add medicines.
                      </p>

                    </div>

                  ) : (

                    <div className="table-responsive">

                      <table className="table align-middle">

                        <thead>
                          <tr>
                            <th>Medicine</th>
                            <th>Dosage</th>
                            <th>Frequency</th>
                            <th>Duration</th>
                            <th>Quantity</th>
                            <th>Instructions</th>
                            <th>Actions</th>
                          </tr>
                        </thead>

                        <tbody>

                          {medicines.map(medicine => (

                            <tr key={medicine.prescriptionItemId}>

                              <td className="fw-semibold">
                                {medicine.medicineName}
                              </td>

                              <td>{medicine.dosage}</td>

                              <td>{medicine.frequency}</td>

                              <td>{medicine.durationDays} days</td>

                              <td>{medicine.quantity}</td>

                              <td>
                                {medicine.instructions || '-'}
                              </td>

                              <td style={{ minWidth: '130px' }}>

                                <button
                                  className="btn btn-sm btn-outline-primary me-1 mb-1"
                                  onClick={() => editMedicine(medicine)}
                                >
                                  Edit
                                </button>

                                <button
                                  className="btn btn-sm btn-outline-danger mb-1"
                                  onClick={() =>
                                    deleteMedicine(medicine.prescriptionItemId)
                                  }
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

              </>
            )}

          </div>
        )}

      </div>

    </div>
  )
}

export default DoctorPrescriptions
