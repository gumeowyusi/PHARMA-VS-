@echo off
setlocal

REM Maven: bo nho cho qua trinh compile (tranh tre / loi khi bien dich lau)
if not defined MAVEN_OPTS set "MAVEN_OPTS=-Xmx1024m -XX:+UseParallelGC"

REM Chay app: run-win.ps1 se goi mvnw voi -DskipTests (bo qua test, nhanh hon)
REM Muon chay kem test: powershell -File run-win.ps1 -SkipTests:$false

set "SCRIPT_DIR=%~dp0"
set "PS1_FILE=%SCRIPT_DIR%run-win.ps1"

if not exist "%PS1_FILE%" (
  echo [X] Khong tim thay run-win.ps1 trong thu muc: %SCRIPT_DIR%
  pause
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%PS1_FILE%" %*
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" (
  echo.
  echo [X] Run nhanh that bai. Exit code: %EXIT_CODE%
  pause
  exit /b %EXIT_CODE%
)

echo.
echo [OK] Run nhanh hoan tat.
pause
exit /b 0
