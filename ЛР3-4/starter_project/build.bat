@echo off
setlocal
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo JDK 17 or newer is required. Add its bin directory to PATH.
  exit /b 1
)
if not exist out mkdir out
javac -Xlint:-options -encoding UTF-8 --release 8 -d out src/ru/edu/pr01/OrderCalculator.java src/ru/edu/pr01/SelfCheck.java src/ru/edu/pr01/BoundaryCheck.java
if errorlevel 1 exit /b 1
jar cfe OrderCalculator.jar ru.edu.pr01.OrderCalculator -C out .
exit /b %ERRORLEVEL%
