param(
    [switch]$InstallMaven = $false
)

$ErrorActionPreference = "Stop"

function Write-Step($message) {
    Write-Host ""
    Write-Host "==> $message" -ForegroundColor Cyan
}

function Test-Command($name) {
    return [bool](Get-Command $name -ErrorAction SilentlyContinue)
}

function Install-WithWinget($id, $label) {
    Write-Host "Dang cai $label ..."
    winget install --id $id --accept-package-agreements --accept-source-agreements -e
}

Set-Location $PSScriptRoot

Write-Host "==============================================="
Write-Host " PHARMA-VS - Cai dat moi truong Windows"
Write-Host "==============================================="

Write-Step "Kiem tra winget"
if (-not (Test-Command "winget")) {
    Write-Host "[X] Khong tim thay winget. Hay cap nhat Microsoft Store/App Installer roi chay lai."
    exit 1
}
Write-Host "[OK] winget san sang"

Write-Step "Kiem tra Java 21"
$javaVersionOk = $false
if (Test-Command "java") {
    $javaVersionOutput = java -version 2>&1
    if ($javaVersionOutput -match 'version "21') {
        $javaVersionOk = $true
    }
}

if (-not $javaVersionOk) {
    Install-WithWinget "EclipseAdoptium.Temurin.21.JDK" "Java 21 (Temurin)"
} else {
    Write-Host "[OK] Java 21 da duoc cai"
}

Write-Step "Kiem tra Docker Desktop"
$dockerInstalled = Test-Path "C:\Program Files\Docker\Docker\Docker Desktop.exe"
if (-not $dockerInstalled) {
    Install-WithWinget "Docker.DockerDesktop" "Docker Desktop"
    Write-Host "[!] Neu he thong yeu cau restart, hay restart may truoc khi chay project."
} else {
    Write-Host "[OK] Docker Desktop da duoc cai"
}

if ($InstallMaven) {
    Write-Step "Kiem tra Maven"
    if (-not (Test-Command "mvn")) {
        Install-WithWinget "Apache.Maven" "Apache Maven"
    } else {
        Write-Host "[OK] Maven da duoc cai"
    }
} else {
    Write-Step "Bo qua Maven"
    Write-Host "Project co san mvnw.cmd, khong bat buoc cai Maven global."
}

Write-Step "Kiem tra Docker CLI"
if (-not (Test-Command "docker")) {
    Write-Host "[!] Docker CLI chua co trong PATH (thuong can mo terminal moi hoac restart)."
} else {
    Write-Host "[OK] Docker CLI san sang"
}

Write-Step "Hoan tat"
Write-Host "1) Mo Docker Desktop va doi trang thai Running."
Write-Host "2) Mo terminal moi."
Write-Host "3) Chay script nhanh: .\run-win.ps1"
