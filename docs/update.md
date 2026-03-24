# 🔄 System Update Guide

This document defines the procedure for upgrading to a new software version while minimizing the risk of data loss or extended downtime.

---

## 1. Pre-Update Preparation

### 1.1 Create Backups

Before making any changes, create backups of all critical data.

**Database dump:**

```bash
docker exec rivalry-db pg_dump -U user rivarly_db > ./backups/pre-update-$(date +%F).sql
```

**Configuration files:**

Copy the current `.env` and `docker-compose.yml` to a safe location outside the project directory:

```bash
cp .env ./backups/.env.bak
cp docker-compose.yml ./backups/docker-compose.yml.bak
```

### 1.2 Verify Compatibility

Before pulling any new code, check the following:

- The new API version is compatible with the current database schema
- The new version does not require an updated Docker image for PostgreSQL or MinIO.
- Any new environment variables introduced in the new version are identified and added to `.env` before startup.

### 1.3 Plan the Maintenance Window

This project requires a brief downtime window during container restart and database migrations. Typical duration: **2–5 minutes**.

Notify relevant stakeholders before proceeding.

---

## 2. Update Process

### Step 1 — Stop the API Container

Stop only the application container, leaving the database and MinIO running to preserve data:

```bash
docker-compose stop api
```

### Step 2 — Pull New Code or Images

**If pulling from a Docker Registry:**

```bash
docker-compose pull api
```

**If building locally:**

```bash
git pull origin main
docker build -t rivalry-api:latest .
```

### Step 3 — Update Configuration (if needed)

If the new version introduces new environment variables, add them to `.env` now — before starting the container.

### Step 4 — Start the API and Monitor Migrations

The application runs database migrations automatically on startup. Start the container and watch the logs to confirm migrations complete successfully:

```bash
docker-compose up -d api
docker logs -f rivalry-api
```

Look for migration completion messages and the absence of any `ERROR` or `Migration failed` entries.

---

## 3. Rollback Procedure

If the system is unstable or fails to start after the update, follow these steps to restore the previous state.

### Step 1 — Revert to the Previous Docker Image

Switch the API container back to the last known stable image tag:

```bash
docker-compose stop api
# Edit docker-compose.yml or docker-compose.prod.yml:
# change image: rivalry-api:latest → rivalry-api:v1.0.pre-update
docker-compose up -d api
```

### Step 2 — Restore the Database (if migrations corrupted data)

> ⚠️ This will **overwrite current database state**. Only proceed if data integrity is compromised.

```bash
docker-compose stop api
docker exec -i rivalry-db psql -U user rivarly_db < ./backups/pre-update-YYYY-MM-DD.sql
docker-compose start api
```

Replace `YYYY-MM-DD` with the actual date of your backup file.

### Step 3 — Verify Rollback Success

Confirm the previous version is running and accessible:

```bash
curl https://your-domain.com/actuator/health
# Expected: { "status": "UP" }

docker logs rivalry-api
# No connection errors or migration failures
```

---

## 4. Quick Reference

| Step | Command |
|------|---------|
| Backup database | `docker exec rivalry-db pg_dump -U user rivarly_db > ./backups/pre-update-$(date +%F).sql` |
| Stop API only | `docker-compose stop api` |
| Pull new image | `docker-compose pull api` |
| Build locally | `git pull origin main && docker build -t rivalry-api:latest .` |
| Start & watch logs | `docker-compose up -d api && docker logs -f rivalry-api` |
| Rollback image | Edit compose file → change image tag → `docker-compose up -d api` |
| Restore DB backup | `docker exec -i rivalry-db psql -U user rivarly_db < ./backups/pre-update-YYYY-MM-DD.sql` |