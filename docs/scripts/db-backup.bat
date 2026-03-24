@echo off
if not exist backups mkdir backups
set TIMESTAMP=%date:~10,4%%date:~4,2%%date:~7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
docker exec rivalry-db pg_dump -U user rivarly_db > ./backups/db_backup_%TIMESTAMP%.sql