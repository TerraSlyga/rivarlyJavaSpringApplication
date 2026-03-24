@echo off
docker-compose up -d db minio
call mvnw.cmd clean install -DskipTests
call mvnw.cmd spring-boot:run