# 🗄️ Backup & Recovery Policy

This document describes the project's data protection strategy against loss caused by technical failures, human error, or cyberattacks.

---

## 1. Backup Strategy

### 1.1 Backup Types & Schedule

| Type | Scope | Frequency |
|------|-------|-----------|
| **Full Backup** | Entire database + file storage | Daily at 03:00 |
| **Differential Backup** | Changes since last full backup | Every 6 hours |
| **System Logs** | Application & container logs | Every hour → archive storage |

### 1.2 Retention & Rotation

**Local storage** (on the application server):

- Last **7 days** of backups are kept locally.


**Rotation policy:**

| Backup Frequency | Retention Period |
|------------------|-----------------|
| Daily backups | 30 days |
| Monthly backups | 1 year |

---

## 2. Backup Procedures

### 2.1 Database (PostgreSQL)

Use `pg_dump` inside the running Docker container:

```bash
docker exec rivalry-db pg_dump -U user rivarly_db > backup_db_$(date +%F).sql
```

### 2.2 User Files (MinIO)

Since files are stored in a Docker Volume, use the MinIO Client (`mc`) or archive the data directory directly:

```bash
tar -czvf backup_files_$(date +%F).tar.gz /path/to/minio/data
```

### 2.3 Configuration Files & Logs

Manually copy the following files to the backup destination:

- `.env`
- `docker-compose.yml`
- `nginx.conf`

Logs can be collected from the `./logs` directory or exported via Docker:

```bash
docker logs rivalry-api > ./backups/api-logs-$(date +%F).log
```

---

## 3. Automation & Integrity

### 3.1 Automated Backup Script

Create the file `docs/scripts/backup-cron.sh` with the following content:

```bash
#!/bin/bash
BACKUP_DIR="./backups/$(date +%F)"
mkdir -p "$BACKUP_DIR"

# Database backup
docker exec rivalry-db pg_dump -U user rivarly_db > "$BACKUP_DIR/db.sql"

# Configuration backup
cp .env "$BACKUP_DIR/.env"
cp docker-compose.yml "$BACKUP_DIR/docker-compose.yml"

# Integrity checksum
sha256sum "$BACKUP_DIR/db.sql" > "$BACKUP_DIR/checksum.sha256"

echo "Backup completed: $BACKUP_DIR"
```

Make it executable and register it as a cron job:

```bash
chmod +x docs/scripts/backup-cron.sh

# Run daily at 03:00
crontab -e
# Add the following line:
0 3 * * * /path/to/project/docs/scripts/backup-cron.sh >> /var/log/backup.log 2>&1
```

### 3.2 Integrity Verification

Every backup is accompanied by a `.sha256` checksum file. To verify a backup before restoring:

```bash
sha256sum -c ./backups/YYYY-MM-DD/checksum.sha256
```

> **Policy requirement:** Once per month, a DevOps engineer must perform a test restore from a backup to validate data integrity. Results should be recorded in the team's operations log.

---

## 4. Recovery Procedures

### 4.1 Full System Recovery

Follow these steps in order to fully restore the system from a backup:

**1. Stop all services:**

```bash
docker-compose down
```

**2. Restore configuration files:**

Copy `.env`, `docker-compose.yml`, and `nginx.conf` back to the project root from the backup directory.

**3. Start database and storage containers:**

```bash
docker-compose up -d db minio
```

**4. Import the database dump:**

```bash
cat ./backups/YYYY-MM-DD/db.sql | docker exec -i rivalry-db psql -U user rivarly_db
```

**5. Restore user files to the MinIO volume:**

```bash
tar -xzvf ./backups/YYYY-MM-DD/backup_files_YYYY-MM-DD.tar.gz -C /path/to/minio/data
```

**6. Start the application:**

```bash
docker-compose up -d api
docker logs -f rivalry-api
```

Confirm the API is healthy:

```bash
curl https://your-domain.com/actuator/health
# Expected: { "status": "UP" }
```

### 4.2 Selective File Recovery (MinIO)

To restore a single accidentally deleted file from MinIO:

1. Extract the relevant backup archive to a temporary directory:

```bash
tar -xzvf ./backups/YYYY-MM-DD/backup_files_YYYY-MM-DD.tar.gz -C /tmp/minio-restore
```

2. Locate the required object within the extracted directory.
3. Copy it back manually via the **MinIO Console** (`http://your-server:9001`) or using the MinIO Client:

```bash
mc cp /tmp/minio-restore/path/to/object myminio/bucket-name/
```

---

## 5. Quick Reference

| Task | Command |
|------|---------|
| Full DB backup | `docker exec rivalry-db pg_dump -U user rivarly_db > backup_db_$(date +%F).sql` |
| Archive MinIO data | `tar -czvf backup_files_$(date +%F).tar.gz /path/to/minio/data` |
| Generate checksum | `sha256sum db.sql > checksum.sha256` |
| Verify checksum | `sha256sum -c checksum.sha256` |
| Restore DB | `cat db.sql \| docker exec -i rivalry-db psql -U user rivarly_db` |
| Restore MinIO files | `tar -xzvf backup_files.tar.gz -C /path/to/minio/data` |
| Run backup script | `./docs/scripts/backup-cron.sh` |