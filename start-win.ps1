param(
    [switch]$InstallMaven = $false,
    [int]$SqlWaitTimeoutSec = 120
)

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host ""
Write-Host "==============================================="
Write-Host " PHARMA-VS - Setup + Run (All-in-one)"
Write-Host "==============================================="
Write-Host ""

if (-not (Test-Path ".\install-win.ps1")) {
    throw "Khong tim thay install-win.ps1"
}

if (-not (Test-Path ".\run-win.ps1")) {
    throw "Khong tim thay run-win.ps1"
}

Write-Host "Buoc 1/2: Cai dat moi truong..."
if ($InstallMaven) {
    & ".\install-win.ps1" -InstallMaven
} else {
    & ".\install-win.ps1"
}

Write-Host ""
Write-Host "Buoc 2/2: Khoi dong project..."
& ".\run-win.ps1" -SqlWaitTimeoutSec $SqlWaitTimeoutSec
