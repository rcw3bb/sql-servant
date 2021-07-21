@echo off

rem Requires Java 11

set SCRIPT_DIR=%~dp0
set LIBS_DIR=%SCRIPT_DIR%libs
set LIBS=@libraries@

rem Add jdbc drivers here
set DRVS_DIR=%SCRIPT_DIR%drivers
set DRVS=@drivers@

set MAIN_JAR=%LIBS_DIR%\sql-servant-@app.version@.jar
set CLASSPATH=%SCRIPT_DIR%;%LIBS%;%MAIN_JAR%;%DRVS%

cd /d %SCRIPT_DIR%

java -cp %CLASSPATH% @java.library.path@ sqlsrvnt %*