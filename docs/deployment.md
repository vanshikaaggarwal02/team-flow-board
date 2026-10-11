# Deploy TeamFlow for a portfolio demo

This guide deploys the existing `render.yaml` Blueprint: a React static site and a Dockerized Spring Boot API on Render, with PostgreSQL on Neon. It uses the local sample database so the public app has projects, members, tasks, and history from the start.

## Hosting choice and trade-offs

- **Render Static Site:** serves the built React app over HTTPS and a global CDN. Static sites are currently free to deploy.
- **Render Web Service:** runs the Spring Boot API from `backend/Dockerfile` and exposes `/api/health` for health checks.
- **Neon PostgreSQL:** stores accounts and project data. This guide uses Neon because a free Render PostgreSQL database currently expires after 30 days. Check the provider dashboards before provisioning; plans and quotas change.
- **Free-service behavior:** Render free web services spin down after 15 minutes without requests and take about a minute to wake. Render also has monthly bandwidth/build limits; if you have no payment method and exceed included usage, it disables services/builds rather than billing you. Neon currently lists a Free plan with finite storage and compute quotas. These plans can change, so check the dashboards before provisioning. See [Render's free plan limits](https://render.com/docs/free), [Render billing FAQ](https://render.com/docs/faq), and [Neon's current plans](https://neon.com/pricing).

## Before provisioning

1. Push the source to your GitHub account by following [Step 0](#0-push-the-project-to-github).
2. Check `git status` and confirm no `.env`, passwords, database dumps, or real user data are staged. `.gitignore` excludes environment files and build output.
3. The deployment is public and the dataset is intentionally fake. The application currently permits members to edit tasks, and it does not provide a read-only recruiter role. Treat demo data as mutable and re-import the backup to restore it if needed.

## 0. Push the project to GitHub

This workspace checkout may be missing Git metadata. If `git status` says it is not a repository, follow these steps from the TeamFlow project root:

1. Create a **new empty** repository on GitHub. Do not initialize it with a README, license, or `.gitignore`.
2. In Terminal, go to the project folder and initialize Git:

   ```bash
   cd /path/to/teamflow
   git init
   git branch -M main
   git add .
   git status --short
   ```

3. Review the staged file list. It should not contain `.env` files, SQL dumps, database backups, or credentials. If any secret file appears, unstage it with `git rm --cached <path>` and remove it from the list before continuing. Confirm the env templates contain placeholders only.
4. Commit and connect the empty GitHub repository (replace the URL with the one GitHub shows for your account):

   ```bash
   git commit -m "Prepare TeamFlow portfolio deployment"
   git remote add origin https://github.com/<your-account>/<your-repository>.git
   git push -u origin main
   ```

   GitHub may prompt you to authenticate in a browser or with GitHub CLI. Do not put a GitHub token in the remote URL.
5. If you already have this project in a Git repository, use that checkout instead: review `git status`, commit the intended changes, then push the connected branch. Do not run `git init` inside an existing repository.

The application does not contain database or demo plaintext passwords in source. Local `.env` files and the exported demo SQL are ignored. If any real credential was ever committed in an earlier repository history, rotate it; deleting it in a later commit does not remove it from Git history. Demo account passwords should be shared privately with recruiters, not written in README, code, or public issue comments.

## 1. Create a Neon PostgreSQL database

1. Create a Neon project and database in a region close to the Render API region.
2. In the Neon connection details, select the Java/JDBC connection format if offered. TeamFlow expects a JDBC URL in `DB_URL`, plus separate `DB_USERNAME` and `DB_PASSWORD` values.
3. Use SSL for the remote database. A JDBC URL has this shape:

   ```text
   jdbc:postgresql://<neon-host>/<database>?sslmode=require
   ```

4. Keep the host, database name, role, and password available for the Render setup. Do not put them in the repository.

## 2. Deploy the Render Blueprint

1. In Render, choose **New → Blueprint** and connect the TeamFlow GitHub repository.
2. Select the branch to deploy. Render reads the root `render.yaml` and prepares the API and static frontend services.
3. For the API's requested secret values, enter:
   - `DB_URL`: the Neon JDBC URL.
   - `DB_USERNAME`: the Neon database role.
   - `DB_PASSWORD`: that role's password.
4. Keep the Blueprint's plans on `free` and do not upgrade services. To reduce surprise billing risk, do not add a payment method if Render allows you to deploy without one; its current docs say over-limit services/builds are suspended when no payment method is on file. Keep an eye on Render and Neon usage dashboards. Free-tier limits and terms can change, so this setup cannot guarantee zero cost indefinitely if account settings or provider plans change.
5. Create the Blueprint and wait for the services to finish deploying.

The Blueprint wires the frontend's `VITE_API_BASE_URL` to the API's Render URL and the API's `FRONTEND_ORIGIN` to the frontend's Render URL. It also sets `COOKIE_SECURE=true`, configures the API health check at `/api/health`, and adds the SPA rewrite so direct navigation works.

Flyway applies the initial schema migration when the API connects to the empty Neon database. Do not set `SPRING_PROFILES_ACTIVE=demo` for this import workflow; the sample dataset already contains the demo users and the import below preserves their existing password hashes.

## 3. Import the local demo dataset

Wait until the API deploy is healthy so Flyway has created the tables in Neon. Then export the local data and import it into the hosted database. These commands prompt for the database password; they do not put the password in shell history.

### Export from local PostgreSQL

From a terminal where PostgreSQL is running:

```bash
pg_dump --data-only --no-owner --no-privileges \
  --exclude-table=public.flyway_schema_history \
  --host=localhost --port=5432 --username=teamflow --dbname=teamflow \
  --file=/tmp/teamflow-demo-data.sql --password
```

This file contains account records and password hashes as well as project/task rows. Keep it local, do not commit it, and only use it if the local database contains the intended sample data.

### Import to Neon

Use the host, role, and database name from Neon. `PGSSLMODE=require` forces an encrypted connection:

```bash
PGSSLMODE=require psql \
  --host=<neon-host> --port=5432 --username=<neon-role> --dbname=<neon-database> \
  --set=ON_ERROR_STOP=1 --file=/tmp/teamflow-demo-data.sql --password
```

Enter the Neon role password at the prompt. The data-only dump keeps the Flyway migration history out of the import and includes sequence values so new records continue to receive valid IDs.

After import, verify the app's project list, project member counts, task statuses, and task histories. If the dump fails partway through, inspect the psql error before retrying; do not blindly import over partially inserted data.

## 4. Verify the live app

1. Open the frontend URL from the Render dashboard.
2. Open `<API URL>/api/health`; expect a JSON response with `status: ok`.
3. Sign in with a demo account from the local dataset. The reusable demo logins use `demo.user001@example.test` through `demo.user100@example.test`. Retrieve the demo password from your private credential store or set/reset it yourself; do not put it in the repository. A demo member sees projects they belong to. Do not share the project owner's login unless you intend visitors to have owner permissions.
4. Check these flows: project overview, task list, board drag and drop, task edit/history, member list, analytics, profile, logout, and sign-in again.
5. Check browser developer tools for CORS, cookie, or CSRF errors. `FRONTEND_ORIGIN` must match the static site's exact HTTPS origin, with no trailing slash.
6. Test the API after it has been idle. A delayed first response is expected on a free Render web service as it wakes.

## Environment variables

| Service | Variable | Value/source |
|---|---|---|
| API | `DB_URL` | Neon JDBC URL including `sslmode=require`. |
| API | `DB_USERNAME` | Neon role name. |
| API | `DB_PASSWORD` | Neon role password; secret. |
| API (optional) | `DEMO_USER_PASSWORD` | Required only when running the `demo` profile to create missing demo accounts; keep in a local secret store. |
| API | `FRONTEND_ORIGIN` | Derived by the Blueprint from the static site's Render URL. |
| API | `COOKIE_SECURE` | `true` for HTTPS. |
| API | `PORT` | Supplied by Render and used by the Docker entrypoint. |
| Frontend build | `VITE_API_BASE_URL` | Derived by the Blueprint from the API's Render URL. |

## Updating the deployment

Push a commit to the connected branch. Render rebuilds the frontend and Docker API from the repository. Database changes are applied through new Flyway migrations when the API starts. Back up the hosted data before deploying a migration that changes persisted data.

## Troubleshooting

| Symptom | Check |
|---|---|
| API health check fails | Render logs, `DB_URL` JDBC scheme, Neon credentials, SSL, and database availability. |
| Browser reports CORS 403 | Exact `FRONTEND_ORIGIN` URL and corresponding redeploy. It must not end with `/`. |
| Login works locally but not in deployment | `COOKIE_SECURE=true`, HTTPS origins, credentialed CORS, and CSRF requests. |
| Frontend calls localhost | `VITE_API_BASE_URL` was missing or the static site was not rebuilt after it changed. |
| First request is slow | Free API service was sleeping; wait for its wake-up and reload if needed. |
| Imported IDs conflict | The destination must be a fresh TeamFlow schema with no app data before importing the dump. |

## Provider references

- [Render Blueprint YAML reference](https://render.com/docs/blueprint-spec)
- [Render environment variables and secrets](https://render.com/docs/configure-environment-variables)
- [Render free plan limits](https://render.com/docs/free)
- [Render static sites](https://render.com/docs/static-sites)
- [Neon pricing and current quotas](https://neon.com/pricing)
- [Neon JDBC connection guidance](https://neon.com/docs/connect/connect-from-any-app)
