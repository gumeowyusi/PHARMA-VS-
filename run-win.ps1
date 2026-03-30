param(
    [int]$SqlWaitSec = 25,
    [bool]$SkipTests = $true
)

# Maven: tang bo nho cho JVM compile (giam tre / loi khi compile nhieu file)
if (-not $env:MAVEN_OPTS) {
    $env:MAVEN_OPTS = "-Xmx1024m -XX:+UseParallelGC"
}

Set-Location $PSScriptRoot

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host " PHARMA-VS - Khoi dong nhanh (Windows)"       -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# ---------- Kiem tra file bat buoc ----------
foreach ($f in @(".\docker-compose.yml",".\mvnw.cmd",".\sql\init-docker.sql")) {
    if (-not (Test-Path $f)) {
        Write-Host "[X] Khong tim thay: $f" -ForegroundColor Red; exit 1
    }
}

# ---------- Kiem tra Docker CLI ----------
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "[X] Docker chua duoc cai. Chay install-win.ps1 truoc." -ForegroundColor Red; exit 1
}

# ---------- Bat Docker Desktop neu daemon chua chay ----------
$dockerOk = $false
try { docker info 2>&1 | Out-Null; $dockerOk = ($LASTEXITCODE -eq 0) } catch {}

if (-not $dockerOk) {
    $exe = "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    if (Test-Path $exe) {
        Write-Host "Dang mo Docker Desktop, vui long cho..." -ForegroundColor Yellow
        Start-Process $exe
        Write-Host "Doi Docker daemon (toi da 3 phut)..."
        $t = 0
        while ($t -lt 180) {
            Start-Sleep -Seconds 6; $t += 6
            try { docker info 2>&1 | Out-Null; if ($LASTEXITCODE -eq 0) { $dockerOk = $true; break } } catch {}
            Write-Host "  ...${t}s"
        }
    }
    if (-not $dockerOk) {
        Write-Host "[X] Docker daemon chua san sang. Mo Docker Desktop roi thu lai." -ForegroundColor Red; exit 1
    }
}
Write-Host "[OK] Docker dang chay" -ForegroundColor Green

# ---------- Khoi dong SQL Server ----------
Write-Host ""
Write-Host "Khoi dong SQL Server (Docker)..." -ForegroundColor Cyan
docker compose up -d sqlserver
if ($LASTEXITCODE -ne 0) {
    Write-Host "[X] docker compose up that bai" -ForegroundColor Red; exit 1
}

Write-Host "Cho SQL Server khoi dong ($SqlWaitSec giay)..."
$step = 5
for ($i = $step; $i -le $SqlWaitSec; $i += $step) {
    Start-Sleep -Seconds $step
    Write-Host "  ...${i}/${SqlWaitSec}s"
}
Write-Host "[OK] SQL Server da khoi dong" -ForegroundColor Green

# ---------- Tim duong dan sqlcmd trong container ----------
$sqlcmdPath = ""
foreach ($p in @("/opt/mssql-tools18/bin/sqlcmd", "/opt/mssql-tools/bin/sqlcmd")) {
    $check = docker exec dant-sqlserver test -f $p 2>&1
    if ($LASTEXITCODE -eq 0) { $sqlcmdPath = $p; break }
}

if ($sqlcmdPath -eq "") {
    Write-Host "[!] Khong tim thay sqlcmd trong container. Bo qua init schema." -ForegroundColor Yellow
} else {
    Write-Host "[OK] sqlcmd: $sqlcmdPath" -ForegroundColor Green

    # Kiem tra database STORE da ton tai chua
    $dbCheck = docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C `
        -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.databases WHERE name='STORE'" -h -1 2>&1
    $dbExists = ($dbCheck -match "^\s*1\s*$")

    # Kiem tra bang USERS da ton tai chua (schema da init)
    $schemaInited = $false
    if ($dbExists) {
        $tblCheck = docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C `
            -d STORE -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.objects WHERE object_id=OBJECT_ID('dbo.USERS') AND type='U'" -h -1 2>&1
        $schemaInited = ($tblCheck -match "^\s*1\s*$")
    }

    if (-not $schemaInited) {
        Write-Host ""
        Write-Host "Lan dau: dang nap schema database STORE..." -ForegroundColor Yellow
        $sqlFile = Resolve-Path ".\sql\init-docker.sql"
        Get-Content $sqlFile -Raw | docker exec -i dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Init schema thanh cong!" -ForegroundColor Green
        } else {
            Write-Host "[!] Init schema co loi. Kiem tra log tren." -ForegroundColor Yellow
        }
    } else {
        Write-Host "[OK] Schema da ton tai, bo qua init." -ForegroundColor Green
    }

    # Migration nho: bo sung role STAFF (nhanvien) cho USERS neu chua co
    $staffMigration = ".\sql\add-staff-role-column.sql"
    if (Test-Path $staffMigration) {
        Write-Host "Ap dung migration role STAFF..." -ForegroundColor Cyan
        $migrationFile = Resolve-Path $staffMigration
        Get-Content $migrationFile -Raw | docker exec -i dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Migration role STAFF da duoc ap dung." -ForegroundColor Green
        } else {
            Write-Host "[!] Migration role STAFF gap loi, tiep tuc chay app." -ForegroundColor Yellow
        }
    }
}

# ---------- Kiem tra Java ----------
Write-Host ""
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "[X] Java chua duoc cai. Chay install-win.ps1 truoc." -ForegroundColor Red; exit 1
}
$jv = java -version 2>&1 | Select-Object -First 1
Write-Host "[OK] $jv" -ForegroundColor Green

# ---------- Chay Spring Boot ----------
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host " Khoi dong Spring Boot..."                      -ForegroundColor Cyan
Write-Host " URL : http://localhost:8080"                   -ForegroundColor Green
Write-Host " Admin: capyboy.dev@gmail.com / Admin@123"      -ForegroundColor Green
Write-Host " Nhan Ctrl+C de dung."
Write-Host " Lan dau bien dich co the 2-8 phut (antivirus/SSD). Cho den khi thay: Started ...Application" -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# ---------- Chuan bi Maven (bypass mvnw PowerShell bug) ----------
$mavenVersion = "3.9.9"
$mavenHome    = "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-$mavenVersion"
$mavenBin     = "$mavenHome\bin\mvn.cmd"
$mavenZipUrl  = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
$mavenZip     = "$env:TEMP\apache-maven-$mavenVersion-bin.zip"

if (-not (Test-Path $mavenBin)) {
    Write-Host ""
    Write-Host "Maven $mavenVersion chua co. Dang tai xuong (~10MB)..." -ForegroundColor Yellow
    try {
        # Dung Invoke-WebRequest thay the PowerShell managed module bi loi
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $mavenZipUrl -OutFile $mavenZip -UseBasicParsing
        Write-Host "Giai nen Maven..." -ForegroundColor Yellow
        if (Test-Path $mavenHome) { Remove-Item $mavenHome -Recurse -Force }
        Add-Type -AssemblyName System.IO.Compression.FileSystem
        $zip = [System.IO.Compression.ZipFile]::OpenRead($mavenZip)
        # Extract vao parent folder, sau do rename
        $parentDir = Split-Path $mavenHome -Parent
        [System.IO.Compression.ZipFileExtensions]::ExtractToDirectory($zip, $parentDir)
        $zip.Dispose()
        # ZipFile giai nen thanh apache-maven-x.x.x, rename lai
        $extracted = Join-Path $parentDir "apache-maven-$mavenVersion"
        if ((Test-Path $extracted) -and ($extracted -ne $mavenHome)) {
            Rename-Item $extracted $mavenHome -Force
        }
        Remove-Item $mavenZip -Force -ErrorAction SilentlyContinue
        Write-Host "[OK] Maven $mavenVersion da san sang." -ForegroundColor Green
    } catch {
        Write-Host "[X] Khong tai duoc Maven: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "    Thu tai tay: $mavenZipUrl" -ForegroundColor Yellow
        Write-Host "    Giai nen vao: $mavenHome" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "[OK] Maven $mavenVersion da co san." -ForegroundColor Green
}

# ---------- Chay Spring Boot ----------
$env:PATH = "$mavenHome\bin;$env:PATH"

$mvnArgs = @("spring-boot:run")
if ($SkipTests) { $mvnArgs = @("-DskipTests") + $mvnArgs }

Write-Host ""
Write-Host "Dang chay: mvn $($mvnArgs -join ' ')" -ForegroundColor Cyan
& "$mavenBin" @mvnArgs
