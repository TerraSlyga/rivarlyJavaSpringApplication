@echo off
if "%~1"=="" (
    echo Usage: db-restore.bat ^<path_to_sql_file^>
    exit /b 1
)
type %1 | docker exec -i rivalry-db psql -U user rivarly_db