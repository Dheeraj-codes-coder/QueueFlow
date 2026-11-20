# QueueFlow

QueueFlow is a Spring Boot REST API for managing clinic operations, doctor appointments, patient queues, consultations, and prescriptions.

## Features

- Clinic, doctor, and patient registration
- Login using email and password with JWT authentication
- Role-based access for doctors and patients
- Doctor weekly schedules and appointment-slot generation
- Patient appointment booking, cancellation, and rescheduling
- Prevention of duplicate bookings and bookings or rescheduling into past time slots
- Patient appointment and prescription history
- Doctor daily appointments, upcoming appointments, consultation history, and dashboard statistics
- Queue check-in, token allocation, calling the next patient, and consultation start/completion
- Prescription creation and medicine-item management
- Ownership checks so users can access only the records they are permitted to manage

## Technology Stack

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- Bean Validation
- MySQL
- JWT using JJWT 0.12.6
- Maven

The project also includes the Spring WebSocket starter dependency.

## Project Structure

```text
src/main/java/com/queueflow/
├── config/       # Security and JWT request filter
├── controller/  # REST API endpoints
├── dto/          # Request and response objects
├── entity/       # JPA entities
├── enums/        # Appointment, queue, role, and slot states
├── exception/    # Application exceptions and global error handling
├── repository/   # Spring Data JPA repositories
└── service/      # Business logic
```

## Requirements

- JDK 25
- MySQL Server
- Git (to clone the repository)

## Local Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Dheeraj-codes-coder/QueueFlow.git
   cd QueueFlow
   ```

2. Create the database in MySQL:

   ```sql
   CREATE DATABASE queueflow_db;
   ```

3. Create `src/main/resources/application.properties` locally. This file is intentionally not committed because it contains environment-specific settings and secrets.

   Example local configuration:

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
   # Do not commit the real key.
   jwt.secret=REPLACE_WITH_YOUR_BASE64_SECRET
   ```

   Use `spring.jpa.hibernate.ddl-auto=update` for local development only. Choose a controlled schema-migration strategy for production.

4. Run the application on Windows:

   ```cmd
   mvnw.cmd spring-boot:run
   ```

   On macOS/Linux:

   ```bash
   ./mvnw spring-boot:run
   ```

5. The API is available at:

   ```text
   http://localhost:8080
   ```

## Build and Test

Run the Maven test lifecycle:

Windows:
```cmd
mvnw.cmd clean test
```

macOS/Linux:
```bash
./mvnw clean test
```

## Authentication and Authorization

Authenticate with `POST /api/auth/login`. A successful login returns a JWT. Send it on protected requests using:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

Tokens are configured to expire after 24 hours. Log in again after expiry.

Registration endpoints are public:

- `POST /api/clinics/register`
- `POST /api/doctors/register`
- `POST /api/patients/register`

Role access is enforced by Spring Security:

- Patient endpoints require the PATIENT role.
- Doctor endpoints require the DOCTOR role, except the public registration endpoint and public GET endpoints for doctor schedules and slots.
- Queue-management endpoints require the DOCTOR role.
- Appointment booking and patient appointment actions require the PATIENT role.
- Prescription endpoints allow PATIENT and DOCTOR roles, with record ownership checked in the application.

## Main API Routes

All request and response bodies use JSON unless otherwise noted.

### Authentication and registration

| Method | Route | Access |
|---|---|---|
| POST | `/api/auth/login` | Public |
| POST | `/api/clinics/register` | Public |
| POST | `/api/doctors/register` | Public |
| POST | `/api/patients/register` | Public |

### Doctor schedules and appointment slots

| Method | Route | Access |
|---|---|---|
| POST | `/api/doctors/{doctorId}/schedule` | Owning doctor |
| GET | `/api/doctors/{doctorId}/schedule` | Public |
| PUT | `/api/doctors/schedule/{scheduleId}` | Owning doctor |
| DELETE | `/api/doctors/schedule/{scheduleId}` | Owning doctor |
| POST | `/api/doctors/{doctorId}/generate-slots` | Owning doctor |
| GET | `/api/doctors/{doctorId}/slots` | Public |

### Patient appointments

| Method | Route | Access |
|---|---|---|
| POST | `/api/appointments/book` | Patient |
| DELETE | `/api/appointments/{appointmentId}/cancel?patientId={patientId}` | Owning patient |
| PUT | `/api/appointments/{appointmentId}/reschedule` | Owning patient |
| GET | `/api/patients/{patientId}/appointments` | Owning patient |

### Doctor views

| Method | Route | Access |
|---|---|---|
| GET | `/api/doctors/{doctorId}/dashboard/statistics?date=YYYY-MM-DD` | Owning doctor |
| GET | `/api/doctors/{doctorId}/appointments/daily?date=YYYY-MM-DD` | Owning doctor |
| GET | `/api/doctors/{doctorId}/appointments/upcoming?date=YYYY-MM-DD` | Owning doctor |
| GET | `/api/doctors/{doctorId}/consultations/history` | Owning doctor |

### Queue and consultations

| Method | Route | Access |
|---|---|---|
| POST | `/api/queue/check-in` | Owning doctor |
| GET | `/api/queue/doctors/{doctorId}/today` | Owning doctor |
| POST | `/api/queue/doctors/{doctorId}/call-next` | Owning doctor |
| POST | `/api/queue/tickets/{queueTicketId}/start-consultation` | Owning doctor |
| POST | `/api/queue/tickets/{queueTicketId}/complete-consultation` | Owning doctor |

### Prescriptions

| Method | Route | Access |
|---|---|---|
| POST | `/api/prescriptions/create` | Owning doctor |
| GET | `/api/prescriptions/{prescriptionId}` | Owning doctor or patient owner |
| GET | `/api/prescriptions/patients/{patientId}/history` | Owning patient |
| POST | `/api/prescriptions/{prescriptionId}/medicines` | Owning doctor |
| GET | `/api/prescriptions/{prescriptionId}/medicines` | Owning doctor or patient owner |
| PUT | `/api/prescriptions/{prescriptionId}/medicines/{prescriptionItemId}` | Owning doctor |
| DELETE | `/api/prescriptions/{prescriptionId}/medicines/{prescriptionItemId}` | Owning doctor |

## Notes

- Slot generation uses the clinic's configured timezone and skips slots whose start time has already passed on the current clinic-local date.
- Booking and rescheduling independently validate that a slot is not in the past.
- Rescheduling creates a replacement appointment record and marks the previous appointment as CANCELLED.
- This repository currently contains the backend API; a separate frontend is not included here.
- Do not commit `application.properties`, database passwords, JWT secrets, access tokens, or real patient data.
