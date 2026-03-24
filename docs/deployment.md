# 🚀 Production Deployment Guide

This document covers the technical requirements and steps for deploying the sports competition support platform in a production environment.

---

## 1. Hardware Requirements

Minimum specifications for stable operation under moderate load:

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| Architecture | x86_64 / ARM64 | x86_64 |
| CPU | 2 cores | 2+ cores @ 2.5 GHz+ |
| RAM | 4 GB (2 GB free for JVM) | 8 GB |
| Disk | 20 GB SSD (logs & local cache) | 40 GB SSD |
| Storage | External volume for MinIO | Dedicated block storage |

---

## 2. Software Prerequisites

The following must be installed on the target server:

- **Docker** `20.10+`
- **Docker Compose** V2
- **Nginx** — as a reverse proxy
- **SSL certificate** — via Certbot / Let's Encrypt

---

## 3. Network Configuration

Open the following firewall ports:

| Port | Protocol | Purpose |
|------|----------|---------|
| `80` | HTTP | Public access (redirects to HTTPS) |
| `443` | HTTPS | Public access |
| `22` | TCP | SSH admin access |
| `9001` | TCP | MinIO Console *(optional — close after initial setup)* |

> **Internal networking:** All services (API, DB, MinIO) must reside within a single isolated Docker network named `backend-net`.

---

## 4. Server & Database Configuration

### 4.1 Create the `.env` File

Before starting the stack, create a `.env` file in the project root with real production secrets. **Never commit this file to version control.**

```env
# ─── DATABASE ─────────────────────────────────────────────
POSTGRES_DB=rivarly_db
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_strong_db_password

# ─── MINIO ────────────────────────────────────────────────
MINIO_ROOT_USER=your_minio_admin
MINIO_ROOT_PASSWORD=your_strong_minio_password

# ─── SPRING SECURITY ──────────────────────────────────────
SPRING_SECURITY_USER_NAME=your_admin_user
SPRING_SECURITY_USER_PASSWORD=your_strong_admin_password

# ─── JWT ──────────────────────────────────────────────────
JWT_SECRET_KEY=your_long_random_secret_key_min_64_chars
```

### 4.2 `application.properties` — Production Settings

Update `src/main/resources/application.properties` (or create a separate `application-prod.properties`) with the following values, referencing your `.env` variables:

```properties
# ─── ACTIVE PROFILE ───────────────────────────────────────
spring.profiles.active=prod

# ─── DATABASE CONNECTION ──────────────────────────────────
spring.datasource.url=jdbc:postgresql://db:5432/${POSTGRES_DB}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}

# ─── MINIO ────────────────────────────────────────────────
minio.url=http://minio:9000
minio.access-key=${MINIO_ROOT_USER}
minio.secret-key=${MINIO_ROOT_PASSWORD}

# ─── SPRING SECURITY ──────────────────────────────────────
spring.security.user.name=${SPRING_SECURITY_USER_NAME}
spring.security.user.password=${SPRING_SECURITY_USER_PASSWORD}
spring.security.user.roles=admin

# ─── JWT ──────────────────────────────────────────────────
jwt.secretKey=${JWT_SECRET_KEY}
```

> ⚠️ **Security notes:**
> - Replace all placeholder values with strong, randomly generated secrets.
> - The JWT secret key should be **at least 64 characters** long.
> - PostgreSQL and MinIO data must be persisted via Docker named volumes (e.g., `/var/lib/postgresql/data`).

---

## 5. Deployment Steps

### Step 1 — Build the Docker Image

```bash
docker build -t rivalry-api:latest .
```

### Step 2 — Start the Production Stack

```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Step 3 — Configure Nginx

Add a server block to your Nginx config to forward HTTPS traffic to the Spring Boot API:

```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate     /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    location / {
        proxy_pass         http://localhost:8080;
        proxy_set_header   Host $host;
        proxy_set_header   X-Real-IP $remote_addr;
        proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }
}

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$host$request_uri;
}
```

Reload Nginx after editing:

```bash
sudo nginx -t && sudo systemctl reload nginx
```

---

## 6. Health Checks

Use the following checks to verify everything is running correctly after deployment.

### ✅ API Status

```bash
curl https://your-domain.com/actuator/health
```

Expected response:

```json
{ "status": "UP" }
```

### ✅ Database Connection

```bash
docker logs rivalry-api
```

A successful connection produces no `Connection refused` errors in the Spring logs.

### ✅ Object Storage (MinIO)

Upload a test avatar via the API. If the file appears in the MinIO bucket, storage is working correctly.

You can also verify directly via the MinIO Console at `http://your-server-ip:9001` *(disable public access to this port after verification)*.

---

## 7. Quick Reference — Useful Commands

| Command | Description |
|---------|-------------|
| `docker-compose -f docker-compose.prod.yml up -d` | Start production stack |
| `docker-compose -f docker-compose.prod.yml down` | Stop production stack |
| `docker logs rivalry-api` | View API logs |
| `docker logs rivalry-db` | View database logs |
| `docker-compose logs -f` | Stream all container logs |
| `sudo certbot renew` | Renew SSL certificate |