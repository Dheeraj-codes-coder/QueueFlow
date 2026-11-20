# QueueFlow

QueueFlow is a clinic appointment and patient-queue management application. It contains a **Spring Boot REST API** and a **React frontend** in one repository.

The application supports separate ADMIN, DOCTOR, and PATIENT workflows for managing clinic users, doctor specialties, schedules, appointments, queues, consultations, and prescriptions.

## Features

### Accounts and access
- Clinic, doctor, and patient registration
- Email/password login with JWT authentication
- Role-based access for ADMIN, DOCTOR, and PATIENT
- Admin user listing and account activation/deactivation
- Admin management of doctor specialties
- Ownership checks for protected patient and doctor records

### Doctor and clinic operations
- Doctor specialties and public doctor browsing/filtering
- Weekly doctor schedules and appointment-slot generation
- Doctor appointment lists, upcoming appointments, and dashboard statistics
- Queue check-in, token allocation, calling the next patient, and consultation start/completion
- Consultation history and prescription creation
- Prescription medicine-item management

### Patient operations
- Patient dashboard and appointment history
- Appointment booking, cancellation, and rescheduling
- Patient queue check-in
- Prescription history and access to permitted prescription details

## Technology Stack

### Backend
- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- Bean Validation
- MySQL
- JWT using JJWT 0.12.6
- Maven
- Spring WebSocket starter dependency

### Frontend
- React 19
- React Router
- Vite
- Axios
- Bootstrap 5
- JavaScript and CSS

## Repository Structure

```text
QueueFlow/
├── src/                         # Spring Boot backend
│   └── main/
│       ├── java/com/queueflow/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── enums/
│       │   ├── exception/
│       │   ├── repository/
│       │   └── service/
│       └── resources/
├── frontend/                    # React + Vite frontend
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── pages/
│   │   └── services/
│   ├── package.json
│   └── vite.config.js
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Requirements

- JDK 25
- MySQL Server
- Node.js and npm
- Git

## Local Setup

Run the backend and frontend in separate terminals. Start the MySQL database before starting the backend.

### 1. Clone the repository

```bash
git clone https://github.com/Dheeraj-codes-coder/QueueFlow.git
cd QueueFlow
```

### 2. Create the MySQL database

In MySQL, run:

```sql
CREATE DATABASE queueflow_db;
```

### 3. Configure the backend

Create `src/main/resources/application.properties` locally. Keep this file out of Git because it contains machine-specific configuration and secrets.

Example for local development:

```properties
spring.application.name=QueueFlow
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/queueflow_db
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Use a securely generated Base64-encoded key with at least 32 decoded bytes.
# Never commit the real key.
jwt.secret=REPLACE_WITH_YOUR_BASE64_SECRET
```

Use `spring.jpa.hibernate.ddl-auto=update` for local development only. Use a controlled schema-migration strategy for production.

### 4. Start the backend

From the repository root:

**Windows CMD**
```cmd
mvnw.cmd spring-boot:run
```

**macOS/Linux**
```bash
./mvnw spring-boot:run
```

The backend API runs at:

```text
http://localhost:8080
```

### 5. Install and start the frontend

Open a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Vite normally serves the frontend at:

```text
http://localhost:5173
```

The frontend API client is configured in `frontend/src/services/api.js` with the local backend URL `http://localhost:8080`. Update this base URL and configure the backend CORS allowed origin when using a different environment or deploying the application.

## Build, Lint, and Test

### Backend

**Windows CMD**
```cmd
mvnw.cmd clean test
mvnw.cmd -DskipTests package
```

**macOS/Linux**
```bash
./mvnw clean test
./mvnw -DskipTests package
```

### Frontend

From the `frontend` directory:

```bash
npm run lint
npm run build
```

To preview a production frontend build locally:

```bash
npm run preview
```

## Authentication

Log in using `POST /api/auth/login`. A successful response includes a JWT. Send the token on protected requests:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

Registration endpoints:

- `POST /api/clinics/register`
- `POST /api/doctors/register`
- `POST /api/patients/register`

The application uses role-based authorization for ADMIN, DOCTOR, and PATIENT users. The frontend routes are defined in `frontend/src/App.jsx`; the backend remains responsible for enforcing authorization and record ownership.

Email/OTP registration verification is not currently part of the implemented registration flow.

## Main API Routes

All request and response bodies use JSON unless otherwise noted.

### Authentication and registration

| Method | Route | Access |
|---|---|---|
| POST | `/api/auth/login` | Public |
| POST | `/api/clinics/register` | Public |
| POST | `/api/doctors/register` | Public |
| POST | `/api/patients/register` | Public |

### Doctor browsing and specialties

| Method | Route | Access |
|---|---|---|
| GET | `/api/doctors` | Public |
| GET | `/api/doctors/specialties` | Public |
| PUT | `/api/doctors/{doctorId}/specialty` | ADMIN |

### Doctor schedules and appointment slots

| Method | Route | Access |
|---|---|---|
| POST | `/api/doctors/{doctorId}/schedule` | Doctor |
| GET | `/api/doctors/{doctorId}/schedule` | Public |
| PUT | `/api/doctors/schedule/{scheduleId}` | Doctor |
| DELETE | `/api/doctors/schedule/{scheduleId}` | Doctor |
| POST | `/api/doctors/{doctorId}/generate-slots` | Doctor |
| GET | `/api/doctors/{doctorId}/slots` | Public |

### Patient appointments and dashboard

| Method | Route | Access |
|---|---|---|
| POST | `/api/appointments/book` | Patient |
| DELETE | `/api/appointments/{appointmentId}/cancel?patientId={patientId}` | Owning patient |
| PUT | `/api/appointments/{appointmentId}/reschedule` | Owning patient |
| GET | `/api/patients/{patientId}/appointments` | Owning patient |
| GET | `/api/patients/me/dashboard` | Patient |

### Doctor dashboard and appointments

| Method | Route | Access |
|---|---|---|
| GET | `/api/doctors/{doctorId}/dashboard/statistics?date=YYYY-MM-DD` | Doctor |
| GET | `/api/doctors/{doctorId}/appointments/daily?date=YYYY-MM-DD` | Doctor |
| GET | `/api/doctors/{doctorId}/appointments/upcoming?date=YYYY-MM-DD` | Doctor |
| GET | `/api/doctors/{doctorId}/consultations/history` | Doctor |

### Queue and consultations

| Method | Route | Access |
|---|---|---|
| POST | `/api/queue/patient/check-in` | Patient |
| POST | `/api/queue/check-in` | Doctor |
| GET | `/api/queue/doctors/{doctorId}/today` | Doctor |
| POST | `/api/queue/doctors/{doctorId}/call-next` | Doctor |
| POST | `/api/queue/tickets/{queueTicketId}/start-consultation` | Doctor |
| POST | `/api/queue/tickets/{queueTicketId}/complete-consultation` | Doctor |

### Prescriptions

| Method | Route | Access |
|---|---|---|
| POST | `/api/prescriptions/create` | Doctor |
| GET | `/api/prescriptions/{prescriptionId}` | Authorized doctor or patient owner |
| GET | `/api/prescriptions/patients/{patientId}/history` | Owning patient |
| POST | `/api/prescriptions/{prescriptionId}/medicines` | Doctor |
| GET | `/api/prescriptions/{prescriptionId}/medicines` | Authorized doctor or patient owner |
| PUT | `/api/prescriptions/{prescriptionId}/medicines/{prescriptionItemId}` | Doctor |
| DELETE | `/api/prescriptions/{prescriptionId}/medicines/{prescriptionItemId}` | Doctor |

### Admin user management

| Method | Route | Access |
|---|---|---|
| GET | `/api/admin/users` | ADMIN |
| PATCH | `/api/admin/users/{userId}/status` | ADMIN |

> The tables summarize the main implemented routes. Consult the controller classes for request fields, response formats, validation rules, and any additional endpoints.

## Important Notes

- Doctor slot generation uses the clinic's configured timezone and skips slots whose start time has already passed on the current clinic-local date.
- Booking and rescheduling validate that a slot is not in the past.
- Rescheduling creates a replacement appointment record and marks the previous appointment as CANCELLED.
- The local frontend is configured to call the backend at `http://localhost:8080`.
- Local setup instructions are for development, not production deployment.
- Before production use, verify authentication, authorization, patient-data privacy, error handling, database migrations, backups, HTTPS, and deployment configuration.
- Never commit `application.properties`, database passwords, JWT secrets, access tokens, or real patient data.

## Repository

[GitHub: Dheeraj-codes-coder/QueueFlow](https://github.com/Dheeraj-codes-coder/QueueFlow)
