# QueueFlow Frontend

QueueFlow Frontend is the React-based user interface for the QueueFlow clinic appointment and patient-queue management application. It communicates with the Spring Boot REST API.

## Features

The frontend currently includes separate screens for the application's main user roles:

- **Admin dashboard**
  - View doctor records and assign doctor specialties
  - View registered users
  - Search and filter users
  - Activate or deactivate eligible user accounts

- **Doctor screens**
  - Doctor dashboard
  - View appointments
  - View upcoming appointments
  - Manage the patient queue

- **Patient screens**
  - Patient dashboard
  - View appointments
  - Book an appointment

- **Authentication**
  - Login screen
  - JWT-based authenticated API requests

The backend is responsible for enforcing authorization and checking record ownership. Hiding or restricting a frontend route is not a substitute for backend security.

## Technology Stack

- React 19
- React Router
- Vite
- Axios
- Bootstrap 5
- JavaScript and CSS

## Requirements

- Node.js
- npm
- QueueFlow Spring Boot backend
- MySQL database configured for the backend

## Setup

### 1. Open the frontend directory

From the repository root:

```bash
cd frontend
```

### 2. Install dependencies

```bash
npm install
```

### 3. Configure the backend URL

The Axios client is configured in:

```text
src/services/api.js
```

The current development configuration uses:

```javascript
baseURL: 'http://localhost:8080'
```

Make sure the Spring Boot backend is running at that address. If the backend uses another address or port, update the base URL and configure the backend CORS allowed origin to match the frontend origin.

### 4. Start the development server

```bash
npm run dev
```

Vite normally starts the frontend at:

```text
http://localhost:5173
```

Open that address in your browser.

## Available Scripts

Run these commands from the `frontend` directory.

| Command | Purpose |
|---|---|
| `npm run dev` | Start the Vite development server |
| `npm run build` | Create an optimized production build in `dist/` |
| `npm run preview` | Preview the production build locally |
| `npm run lint` | Run ESLint |

Before sharing or deploying a frontend build, run:

```bash
npm run lint
npm run build
```

## Application Routes

The routes are configured in `src/App.jsx`.

| Route | Screen |
|---|---|
| `/` | Login |
| `/admin-dashboard` | Admin dashboard |
| `/doctor-dashboard` | Doctor dashboard |
| `/doctor-appointments` | Doctor appointments |
| `/doctor-upcoming-appointments` | Doctor upcoming appointments |
| `/doctor-queue` | Doctor queue |
| `/patient-dashboard` | Patient dashboard |
| `/patient-appointments` | Patient appointments |
| `/book-appointment` | Book appointment |

## Project Structure

```text
frontend/
├── public/                 # Public static assets
├── src/
│   ├── assets/             # Images and frontend assets
│   ├── components/         # Shared UI components
│   ├── context/            # React context
│   ├── pages/              # Admin, doctor, patient and login screens
│   ├── services/
│   │   └── api.js          # Axios API client
│   ├── App.jsx             # Application routes
│   ├── App.css             # Application styles
│   ├── index.css           # Global styles
│   └── main.jsx            # React entry point
├── package.json
├── package-lock.json
└── vite.config.js
```

## Related Backend

The Spring Boot backend is in the repository root. Follow the root [README](../README.md) for MySQL setup, backend configuration, authentication details, and API documentation.

## Repository

[GitHub: Dheeraj-codes-coder/QueueFlow](https://github.com/Dheeraj-codes-coder/QueueFlow)
