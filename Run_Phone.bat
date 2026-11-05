@echo off
TITLE Lensora Wireless ADB Deployment
echo ============================================================
echo            LENSORA - WIRELESS DEBUGGING SETUP
echo ============================================================
echo.

set /p PHONE_IP="Enter Phone IP Address and Port (e.g. 192.168.1.100:5555): "
if "%PHONE_IP%"=="" goto END

echo Connecting to device at %PHONE_IP%...
adb connect %PHONE_IP%

echo.
echo Checking ADB Devices...
adb devices

echo.
echo Building Lensora Debug APK...
call gradlew.bat assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Gradle build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Installing Lensora to device...
adb install -r app\build\outputs\apk\debug\app-debug.apk

echo.
echo Starting Lensora MainActivity...
adb shell am start -n com.project.lensora/.MainActivity

echo.
echo ============================================================
echo Deployment Complete! Lensora is running on your device.
echo ============================================================

:END
pause
