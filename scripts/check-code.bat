@echo off
setlocal enabledelayedexpansion

:: Переходимо на рівень вище до кореня проекту
cd /d "%~dp0.."

echo.
echo ==========================================
echo          CODE QUALITY CHECK
echo ==========================================
echo.

echo === Compiling... ===
call mvn compile > compile_output.tmp 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed! Check compile_output.tmp for details.
    pause
    exit /b %ERRORLEVEL%
)
del compile_output.tmp
echo Compilation done.
echo.

:: --- Checkstyle ---
echo === Checkstyle ===
call mvn checkstyle:check -Dcheckstyle.skip=false -q
if %ERRORLEVEL% neq 0 (
    echo [RESULT] Checkstyle: FAILED
) else (
    echo [RESULT] Checkstyle: PASSED
)

echo.
:: --- SpotBugs ---
echo === SpotBugs ===
call mvn spotbugs:check -Dspotbugs.skip=false -q
if %ERRORLEVEL% neq 0 (
    echo [RESULT] SpotBugs: FAILED
) else (
    echo [RESULT] SpotBugs: PASSED
)

echo.
:: --- PMD ---
echo === PMD ===
call mvn pmd:check -Dpmd.skip=false -q
if %ERRORLEVEL% neq 0 (
    echo [RESULT] PMD: FAILED
) else (
    echo [RESULT] PMD: PASSED
)

echo.
echo ==========================================
echo               Done
echo ==========================================

pause