# Run_Phone.ps1 - Connects physical Android phone via Wi-Fi ADB using Gateway IP

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (!(Test-Path $adb)) {
    $adb = "adb.exe"
}

# 1. Clear stale server states
& $adb kill-server | Out-Null
Start-Sleep -Seconds 1

# 2. Extract the Default Gateway IP dynamically from ipconfig
Write-Host "[*] Detecting Mobile Hotspot Gateway IP..." -ForegroundColor Cyan
$gatewayIp = (Get-NetRoute -DestinationPrefix "0.0.0.0/0" | Select-Object -ExpandProperty NextHop)

if (-not $gatewayIp) {
    Write-Host "[-] Error: Could not detect default gateway. Make sure your hotspot is connected." -ForegroundColor Red
    exit
}

$target = "$gatewayIp`:5555"
Write-Host "[+] Found Hotspot Gateway IP: $gatewayIp" -ForegroundColor Green

# 3. Connect via ADB
Write-Host "[*] Connecting to phone at $target..." -ForegroundColor Cyan
& $adb connect $target

# 4. Verify connection status
& $adb devices
