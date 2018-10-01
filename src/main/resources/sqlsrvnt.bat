@echo off

rem Requires Java 8

set LIBS_DIR=%~dp0\libs
set LIBS=%LIBS_DIR%\commons-cli-1.4.jar;%LIBS_DIR%\commons-dbcp2-2.0.jar;%LIBS_DIR%\commons-io-2.6.jar;%LIBS_DIR%\commons-lang3-3.8.jar;%LIBS_DIR%\commons-logging-1.1.3.jar;%LIBS_DIR%\commons-pool2-2.2.jar;%LIBS_DIR%\groovy-all-2.3.11.jar;%LIBS_DIR%\gson-2.8.5.jar;%LIBS_DIR%\log4j-1.2.17.jar

rem Add jdbc drivers here
set DRVS_DIR==%~dp0\drivers
set DRVS=%DRVS_DIR%\h2-1.4.197.jar

set MAIN_JAR=%LIBS_DIR%\sql-servant-1.0.0-SNAPSHOT.jar
set CLASSPATH=.;%LIBS%;%MAIN_JAR%;%DRVS%

java -cp %CLASSPATH% sqlsrvnt %*