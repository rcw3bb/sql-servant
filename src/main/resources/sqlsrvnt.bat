@echo off

rem Requires Java 8

set LIBS_DIR=%~dp0libs
set LIBS=@libraries@

rem Add jdbc drivers here
set DRVS_DIR=%~dp0drivers
set DRVS=@drivers@

set MAIN_JAR=%LIBS_DIR%\sql-servant-@app.version@.jar
set CLASSPATH=.;%LIBS%;%MAIN_JAR%;%DRVS%

java -cp %CLASSPATH% @java.library.path@ sqlsrvnt %*