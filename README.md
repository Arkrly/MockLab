# MockLab — Stateful API Mocking Sandbox

> Dynamic mock server engine with stateful request simulation, workspace isolation, and multi-role API key authentication for frontend development teams.

---

## ⚠️ Project Status

This project has been **archived**. The original full-stack implementation (Spring Boot backend + Angular frontend) has been moved to separate repositories:

- **Backend**: [mocklab-api](https://github.com/arkrly/mocklab-api)
- **Frontend**: [mocklab-web](https://github.com/arkrly/mocklab-web)

---

## 🏗️ Architecture

MockLab was a full-stack SaaS application composed of two modules:

| Module          | Tech Stack                                     | Repository                |
| --------------- | ---------------------------------------------- | ------------------------- |
| **mocklab-api** | Spring Boot 3.5 · Java 21 · MySQL 8 · JWT      | [arkrly/mocklab-api](https://github.com/arkrly/mocklab-api) |
| **mocklab-web** | Angular 17+ · TypeScript · Tailwind CSS · SCSS | [arkrly/mocklab-web](https://github.com/arkrly/mocklab-web) |

---

## ✨ Features

### Backend (`mocklab-api`)

- **Authentication & Authorization** — JWT-based auth with register/login, API key authentication for consumers
- **Workspace Management** — Create and manage isolated workspaces with role-based member access (`OWNER`, `EDITOR`, `VIEWER`)
- **Mock Endpoint Builder** — Define custom mock endpoints with configurable HTTP method, path, status code, headers, response body, and latency simulation
- **Stateful Request Simulation** — Persist and merge stateful records per endpoint, enabling realistic CRUD mock flows
- **Request Logging** — Automatic logging of all incoming mock requests with headers, body, and timestamps
- **Scheduled Cleanup** — Background scheduler for cleaning up stale stateful records

### Frontend (`mocklab-web`)

- **Auth Module** — Login and registration pages
- **Dashboard** — Workspace listing with quick-access cards
- **Workspace Detail** — View endpoints, request logs, and manage workspace settings
- **Endpoint Creator** — Form-based UI to define mock endpoints
- **Request Logs Viewer** — Searchable, filterable table of captured requests
- **Shared Components** — Status badges, reusable pipes, HTTP interceptors, and route guards

---

## 📁 Archived Project Structure

```
MockAPI/                          # This repository (archived)
├── mocklab-api/                  # [Moved to] arkrly/mocklab-api
│   ├── src/main/java/io/mocklab/api/
│   │   ├── controller/           # REST controllers (Auth, Workspace, Endpoint, MockServer)
│   │   ├── dto/                  # Request & response DTOs
│   │   ├── entity/               # JPA entities (User, Workspace, MockEndpoint, RequestLog, etc.)
│   │   ├── enums/                # HttpMethodType, UserPlan, WorkspaceRole
│   │   ├── exception/            # Global exception handler, custom exceptions
│   │   ├── repository/           # Spring Data JPA repositories
│   │   ├── scheduler/            # Stateful record cleanup scheduler
│   │   ├── security/             # JWT utils, API key filter, Security config
│   │   └── service/              # Business logic services
│   ├── docker-compose.yml        # MySQL 8 dev database
│   └── pom.xml
│
└── mocklab-web/                  # [Moved to] arkrly/mocklab-web
    └── src/app/
        ├── core/                 # Guards, interceptors, models, services
        ├── features/
        │   ├── auth/              # Login & register components
        │   ├── dashboard/         # Dashboard, workspace detail, endpoint creation, request logs
        │   └── settings/          # User settings
        └── shared/                # Reusable components & pipes
```

---

## 🔑 API Overview

| Endpoint                         | Method   | Description                        |
| -------------------------------- | -------- | ---------------------------------- |
| `/api/auth/register`             | POST     | Register a new user                |
| `/api/auth/login`                | POST     | Login and receive JWT              |
| `/api/workspaces`                | GET/POST | List or create workspaces          |
| `/api/workspaces/{id}/endpoints` | GET/POST | List or create mock endpoints      |
| `/api/workspaces/{id}/logs`      | GET      | View request logs                  |
| `/mock/{workspace-slug}/**`      | ANY      | Hit the mock server (API key auth) |

---

## 🛠️ Tech Stack Details

### Backend

- **Spring Boot 3.5.11** — Web, Data JPA, Security, Validation
- **MySQL 8.0** — Primary datastore (Docker Compose)
- **JJWT 0.12.6** — JSON Web Token generation & validation
- **Lombok** — Boilerplate reduction
- **Jackson** — JSON processing for stateful merge engine

### Frontend

- **Angular 17+** — Standalone components architecture
- **TypeScript** — Type-safe development
- **Tailwind CSS + SCSS** — Utility-first styling with custom design system
- **RxJS** — Reactive HTTP communication

---

## 📝 Completed Features

- [x] Full backend API with authentication, workspace, endpoint, and mock server controllers
- [x] JPA entities and repositories for all domain models
- [x] JWT + API key dual authentication system
- [x] Stateful record engine with merge capabilities
- [x] Request logging and scheduled cleanup
- [x] Angular frontend scaffolded with full component tree
- [x] Auth flow (login/register) integrated with backend
- [x] Dashboard with workspace listing and detail views
- [x] Endpoint creation form
- [x] Request logs viewer component
- [x] Shared component library (status badges, pipes)
- [x] HTTP interceptor for automatic JWT attachment
- [x] Route guards for protected pages

---

## 📄 License

This project is for development and demonstration purposes.
