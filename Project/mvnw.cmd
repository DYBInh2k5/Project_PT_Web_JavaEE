@REM ----------------------------------------------------------------------------
@REM Maven Wrapper for Windows — handle paths with spaces and parentheses
@REM ----------------------------------------------------------------------------
@if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

for %%i in ("%APP_HOME%") do set "APP_HOME=%%~fi"

if defined JAVA_HOME goto findJavaFromJavaHome
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
pause
goto fail

:findJavaFromJavaHome
set "JAVA_HOME=%JAVA_HOME:"=%"
set "JAVA_EXE=%JAVA_HOME%/bin/java.exe"
if exist "%JAVA_EXE%" goto init
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
pause
goto fail

:init
set MAVEN_CMD_LINE_ARGS=%*
set "WRAPPER_JAR=%APP_HOME%.mvn\wrapper\maven-wrapper.jar"
set "PROJECT_DIR=%APP_HOME%"

:execute
"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%PROJECT_DIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%

if "%ERRORLEVEL%" == "0" goto end
pause

:end
@endlocal
