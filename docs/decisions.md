# Decisions

- Modular monolith: one deployment and transactional boundary keeps the seven-day MVP tractable while preserving feature ownership.
- PostgreSQL and Flyway: explicit relational constraints and reproducible schema evolution.
- HTTP-only cookie authentication: avoids JavaScript-readable token storage; production must use HTTPS and exact CORS origins.
- No delete endpoint: avoids accidental loss of task history until retention policy is explicitly modeled.
- Board supports both drag-and-drop and a per-card status selector so pointer, keyboard, and direct status changes are available.
- Analytics are derived from current task rows and audit events; completion trends count a task once within the selected date range.
- Demo data is opt-in locally and copied to the public portfolio database explicitly, so the default runtime does not silently seed demo accounts.
