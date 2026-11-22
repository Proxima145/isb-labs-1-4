@echo off
setlocal
cd /d "%~dp0"
java -Dfile.encoding=UTF-8 -cp "OrderCalculator.jar" ru.edu.pr01.SelfCheck
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp "OrderCalculator.jar" ru.edu.pr01.BoundaryCheck
exit /b %ERRORLEVEL%
