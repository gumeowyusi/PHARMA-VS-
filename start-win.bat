@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PS1_FILE=%SCRIPT_DIR%start-win.ps1"

if not exist "%PS1_FILE%" (
  echo [X] Khong tim thay start-win.ps1 trong thu muc: %SCRIPT_DIR%
  pause
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%PS1_FILE%" %*
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" (
  echo.
  echo [X] Script ket thuc voi loi. Exit code: %EXIT_CODE%
  pause
  exit /b %EXIT_CODE%
)

echo.
echo [OK] Hoan tat.
pause
exit /b 0
