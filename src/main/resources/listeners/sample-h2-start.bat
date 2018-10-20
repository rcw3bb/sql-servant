@echo off
if %4=="false" goto continue
echo %~2 : %3 [START]> %~dp0%2.txt
goto exit
:continue
echo %~2 : %3 [CONTINUE]>> %~dp0%2.txt
:exit