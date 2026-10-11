# TeamFlow

**TeamFlow is a project and task management workspace for small teams.** Create projects, bring teammates into a shared workspace, organize work on a Kanban board, and track delivery with project analytics.

> This is a portfolio side project and an MVP. It is not intended for sensitive or production business data.

## What you can do

- Create projects and manage project members and roles.
- Create, assign, edit, search, filter, and prioritize tasks.
- Move tasks between To do, In progress, In review, and Done with drag and drop or status controls.
- Review task activity history and project progress.
- Compare task creation and completion trends, status and priority distributions, and assignee workload.
- Update your profile and choose a light or dark theme.
- Use a responsive layout on desktop and mobile-sized screens.

The notification bell is a placeholder; notification delivery and notification preferences are not implemented.

## Technology

- **Frontend:** React 18, Vite, JavaScript, React Router, Axios, Tailwind CSS tooling, Lucide icons.
- **Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA, Bean Validation, Springdoc OpenAPI.
- **Data:** PostgreSQL with Flyway-managed migrations and task audit history.
- **Quality:** JUnit 5, Mockito, Spring Security Test, Vitest, Testing Library, ESLint.
- **Deployment setup:** Render Blueprint for a static frontend and Dockerized API; Neon PostgreSQL.

## Local development

Prerequisites: Java 21, Node.js 20+, npm, and PostgreSQL.

1. Create a PostgreSQL database and configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in `backend/.env` (start from `backend/.env.example`).
2. In a terminal from `backend`, load the environment and start the API. Flyway applies the schema migration at startup.

   ```bash
   set -a && source .env && set +a
   ./mvnw spring-boot:run
   ```

3. Configure `VITE_API_BASE_URL` in `frontend/.env` (start from `frontend/.env.example`), then run `npm ci && npm run dev` from `frontend`.
4. Open `http://localhost:5173` and register an account.

For repeatable demo logins on a development database, set `SPRING_PROFILES_ACTIVE=demo` and provide `DEMO_USER_PASSWORD` through your local environment. The seeder hashes this secret before storing it and fails if it is missing. Never put the value in source control or use this profile on a database containing personal data.

## Documentation

- [Product overview](docs/product-overview.md) — non-technical explanation of the problem, features, and user experience.
- [Technical reference](docs/technical-reference.md) — architecture, stack, API routes, data model, authorization, and local checks.
- [Deployment guide](docs/deployment.md) — deploy the existing Render Blueprint, connect Neon, and import the demo dataset.
- [Architecture](docs/architecture.md) — application components and request/data flow.
- [Database design](docs/database.md) — entity relationships and persistence notes.

## Development demo accounts

The opt-in `demo` Spring profile creates 100 reusable accounts: `demo.user001@example.test` through `demo.user100@example.test`. Set a strong `DEMO_USER_PASSWORD` locally before starting the profile; the value is not included in this repository. The seeder is idempotent and adds only missing accounts. Never enable this profile for a production or personal-data deployment.

The local demo database contains additional sample projects and tasks. Use the export/import steps in the [deployment guide](docs/deployment.md) to copy that sample dataset to a fresh hosted database.

## Project status

The repository contains the working MVP and deployment configuration, but no hosting resources have been provisioned and there is no live demo URL yet. The deployment guide walks through provisioning the services and verifying the public app.
