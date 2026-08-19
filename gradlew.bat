@echo off
setlocal
where gradle >NUL 2>&1
if %ERRORLEVEL% NEQ 0 (
  echo Gradle was not found on PATH. Install Gradle or use IntelliJ's Gradle integration.
  exit /b 1
)
gradle %*
