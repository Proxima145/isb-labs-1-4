@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out src\ru\edu\pr01\OrderCalculator.java src\ru\edu\pr01\SelfCheck.java src\ru\edu\pr01\BoundaryCheck.java
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp out ru.edu.pr01.SelfCheck
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp out ru.edu.pr01.BoundaryCheck
exit /b %ERRORLEVEL%
