@echo off

rem Requires Java 11

set JAVA_EXE=java.exe
set SCRIPT_DIR=%~dp0
set LIBS_DIR=%SCRIPT_DIR%libs
set LIBS=@libraries@

rem Add jdbc drivers here
set DRVS_DIR=%SCRIPT_DIR%drivers
set DRVS=@drivers@

set MAIN_JAR=%LIBS_DIR%\sql-servant-@app.version@.jar
set CLASSPATH=%SCRIPT_DIR%;%LIBS%;%MAIN_JAR%;%DRVS%

rem Use JAVA_HOME if it exists.
if exist "%JAVA_HOME%" set JAVA_EXE="%JAVA_HOME:"=%\bin\%JAVA_EXE%"

cd /d %SCRIPT_DIR%

%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto run

echo "Java is required"
goto exit

:run
%JAVA_EXE% -cp %CLASSPATH% @java.library.path@ sqlsrvnt %*

:exit