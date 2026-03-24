@echo off
git pull origin main
docker-compose build --no-cache api
docker-compose up -d --force-recreate api
docker image prune -f