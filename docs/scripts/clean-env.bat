@echo off
docker-compose down -v
if exist target rd /s /q target
call mvnw.cmd clean