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

rem Use SS2_JAVA_HOME if it exists
if exist "%SS2_JAVA_HOME%" goto use_ss2_java_home

rem Use JAVA_HOME if it exists.
if exist "%JAVA_HOME%" goto use_java_home

goto use_default_java

:use_ss2_java_home
echo Using SS2_JAVA_HOME
set JAVA_EXE="%SS2_JAVA_HOME:"=%\bin\%JAVA_EXE%"
goto java_version

:use_java_home
echo Using JAVA_HOME
set JAVA_EXE="%JAVA_HOME:"=%\bin\%JAVA_EXE%"
goto java_version

:use_default_java
echo Using default java

:java_version
%JAVA_EXE% -version 2>&1
if "%ERRORLEVEL%" == "0" goto run

echo Java is required
goto exit

:run

pushd %SCRIPT_DIR%
%JAVA_EXE% -DUSER_DATA="%APPDATA%" -cp %CLASSPATH% @java.library.path@ xyz.ronella.tools.sql.servant.SQLServant %*
popd

:exit