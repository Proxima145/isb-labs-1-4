@echo off
setlocal
chcp 65001 >nul
cd /d "%~dp0"
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -jar "OrderCalculator.jar" %*
set "LAB_RESULT=%ERRORLEVEL%"
if "%~1"=="" pause
exit /b %LAB_RESULT%
