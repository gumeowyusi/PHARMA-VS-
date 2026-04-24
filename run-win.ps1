param(
    [int]$SqlWaitSec = 25,
    [bool]$SkipTests = $true
)

# ================================================================
# Detect Java 17+ (Spring Boot 3.x requires Java 17 minimum)
# ================================================================
function Find-Java17Plus {
    $candidates = [System.Collections.Generic.List[string]]::new()

    # 1. Current JAVA_HOME
    if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
        $candidates.Add($env:JAVA_HOME)
    }

    # 2. Windows Registry (most reliable)
    $regPaths = @(
        "HKLM:\SOFTWARE\JavaSoft\JDK",
        "HKLM:\SOFTWARE\JavaSoft\Java Development Kit",
        "HKLM:\SOFTWARE\WOW6432Node\JavaSoft\JDK",
        "HKCU:\SOFTWARE\JavaSoft\JDK"
    )
    foreach ($regPath in $regPaths) {
        if (Test-Path $regPath) {
            Get-ChildItem $regPath -ErrorAction SilentlyContinue | ForEach-Object {
                $jh = (Get-ItemProperty $_.PSPath -ErrorAction SilentlyContinue).JavaHome
                if ($jh -and (Test-Path "$jh\bin\java.exe")) { $candidates.Add($jh) }
            }
        }
    }

    # 3. Common install directories
    $searchDirs = @(
        "$env:ProgramFiles\Eclipse Adoptium",
        "$env:ProgramFiles\Microsoft",
        "$env:ProgramFiles\Java",
        "$env:ProgramFiles\OpenJDK",
        "$env:ProgramFiles\Zulu",
        "$env:ProgramFiles\Amazon Corretto",
        "$env:ProgramFiles\BellSoft\LibericaJDK-21",
        "$env:ProgramFiles\BellSoft\LibericaJDK-17",
        "$env:ProgramW6432\Eclipse Adoptium",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium",
        "$env:LOCALAPPDATA\Programs\OpenJDK",
        "C:\tools\jdk",
        "D:\jdk", "D:\Java"
    )
    foreach ($dir in $searchDirs) {
        if ([string]::IsNullOrEmpty($dir) -or !(Test-Path $dir)) { continue }
        Get-ChildItem $dir -Directory -ErrorAction SilentlyContinue | ForEach-Object {
            if (Test-Path "$($_.FullName)\bin\java.exe") { $candidates.Add($_.FullName) }
        }
    }

    # Pick the highest version >= 17
    $best = $null
    $bestVer = 0
    foreach ($jh in $candidates) {
        $jexe = "$jh\bin\java.exe"
        if (!(Test-Path $jexe)) { continue }
        $verOut = & $jexe -version 2>&1 | Select-Object -First 1
        if ($verOut -match '"([\d]+)\.([\d]+)') {
            $major = [int]$Matches[1]
            if ($major -eq 1) { $major = [int]$Matches[2] }  # 1.8 -> 8
            if ($major -ge 17 -and $major -gt $bestVer) {
                $bestVer = $major; $best = $jh
            }
        }
    }
    return $best
}

Write-Host ""
Write-Host "Dang tim Java 17+..." -ForegroundColor Cyan
$javaHome17 = Find-Java17Plus

if ($javaHome17) {
    $env:JAVA_HOME = $javaHome17
    $env:PATH      = "$javaHome17\bin;$env:PATH"
    $jv = & "$javaHome17\bin\java.exe" -version 2>&1 | Select-Object -First 1
    Write-Host "[OK] Java: $jv" -ForegroundColor Green
    Write-Host "     Path: $javaHome17" -ForegroundColor DarkGray
} else {
    Write-Host ""
    Write-Host "================================================================" -ForegroundColor Yellow
    Write-Host " Khong tim thay Java 17+. Dang thu tu dong cai Java 21..." -ForegroundColor Yellow
    Write-Host "================================================================" -ForegroundColor Yellow

    $wingetOk = $false
    if (Get-Command winget -ErrorAction SilentlyContinue) {
        Write-Host "Chay: winget install EclipseAdoptium.Temurin.21.JDK ..." -ForegroundColor Cyan
        winget install --id EclipseAdoptium.Temurin.21.JDK --accept-source-agreements --accept-package-agreements --silent
        if ($LASTEXITCODE -eq 0) {
            $wingetOk = $true
            Write-Host "[OK] Cai Java 21 thanh cong. Dang tim lai..." -ForegroundColor Green
            $javaHome17 = Find-Java17Plus
            if ($javaHome17) {
                $env:JAVA_HOME = $javaHome17
                $env:PATH = "$javaHome17\bin;$env:PATH"
            }
        }
    }

    if (-not $javaHome17) {
        Write-Host ""
        Write-Host "================================================================" -ForegroundColor Red
        Write-Host " [X] KHONG TIM THAY JAVA 17+!" -ForegroundColor Red
        Write-Host ""
        Write-Host " Spring Boot 3.x YEU CAU Java 17 tro len." -ForegroundColor Red
        Write-Host " May tinh dang dung Java 8 - KHONG tuong thich." -ForegroundColor Red
        Write-Host ""
        Write-Host " CAI JAVA 21 (mien phi) theo 1 trong 2 cach:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host " Cach 1 - Winget (cmd/PowerShell Admin):" -ForegroundColor Green
        Write-Host "   winget install EclipseAdoptium.Temurin.21.JDK" -ForegroundColor White
        Write-Host ""
        Write-Host " Cach 2 - Tai thu cong:" -ForegroundColor Green
        Write-Host "   https://adoptium.net/temurin/releases/?version=21" -ForegroundColor White
        Write-Host "   Chon: Windows | x64 | JDK | .msi" -ForegroundColor White
        Write-Host "   Tick [x] Set JAVA_HOME khi cai" -ForegroundColor White
        Write-Host ""
        Write-Host " Sau khi cai xong, dong cmd nay va chay lai run-win.bat" -ForegroundColor Yellow
        Write-Host "================================================================" -ForegroundColor Red
        pause
        exit 1
    }
}

# Maven memory settings
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
        docker cp ".\sql\init-docker.sql" dant-sqlserver:/tmp/init-docker.sql | Out-Null
        docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -i /tmp/init-docker.sql
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
        docker cp $staffMigration dant-sqlserver:/tmp/add-staff-role-column.sql | Out-Null
        docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -i /tmp/add-staff-role-column.sql
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Migration role STAFF da duoc ap dung." -ForegroundColor Green
        } else {
            Write-Host "[!] Migration role STAFF gap loi, tiep tuc chay app." -ForegroundColor Yellow
        }
    }

    # Migration: OAuth2 + tich diem + tin tuc (dung docker cp de tranh loi encoding)
    $featureMigration = ".\migration.sql"
    if (Test-Path $featureMigration) {
        Write-Host "Ap dung migration OAuth2 / tich diem / tin tuc..." -ForegroundColor Cyan
        docker cp $featureMigration dant-sqlserver:/tmp/migration.sql | Out-Null
        docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -i /tmp/migration.sql
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Migration tinh nang moi da duoc ap dung." -ForegroundColor Green
        } else {
            Write-Host "[!] Migration tinh nang moi gap loi, tiep tuc chay app." -ForegroundColor Yellow
        }
    }

    # Seed du lieu: San pham + Tin tuc (neu chua co)
    $seedProducts = ".\sql\seed-products.sql"
    if (Test-Path $seedProducts) {
        $spCount = docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -d STORE -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM SANPHAM" -h -1 2>$null
        if ([int]($spCount -replace '\s','') -eq 0) {
            Write-Host "Seed san pham va loai..." -ForegroundColor Cyan
            docker cp $seedProducts dant-sqlserver:/tmp/seed-products.sql | Out-Null
            docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -i /tmp/seed-products.sql
        }
    }

    $seedTinTuc = ".\sql\seed-tintuc.sql"
    if (Test-Path $seedTinTuc) {
        $ttCount = docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -d STORE -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM TIN_TUC" -h -1 2>$null
        if ([int]($ttCount -replace '\s','') -eq 0) {
            Write-Host "Seed bai viet tin tuc..." -ForegroundColor Cyan
            docker cp $seedTinTuc dant-sqlserver:/tmp/seed-tintuc.sql | Out-Null
            docker exec dant-sqlserver $sqlcmdPath -S localhost -U sa -P "Nhanhtam456" -C -i /tmp/seed-tintuc.sql
        }
    }
}


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

# ---------- Kill port 8080 neu dang bi chiem ----------
Write-Host ""
Write-Host "Kiem tra port 8080..." -ForegroundColor Cyan
$port = 8080
$netstatLines = netstat -ano 2>$null | Select-String ":$port\s"
$pidsOnPort = $netstatLines |
    ForEach-Object { ($_ -split '\s+')[-1] } |
    Where-Object { $_ -match '^\d+$' -and $_ -ne '0' } |
    Select-Object -Unique

if ($pidsOnPort) {
    foreach ($pid in $pidsOnPort) {
        try {
            $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
            if ($proc) {
                Write-Host "  -> Kill PID $pid ($($proc.ProcessName)) dang chiem port $port" -ForegroundColor Yellow
                Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            }
        } catch {}
    }
    Start-Sleep -Milliseconds 800
    Write-Host "[OK] Port $port da duoc giai phong." -ForegroundColor Green
} else {
    Write-Host "[OK] Port $port san sang." -ForegroundColor Green
}

# ---------- Chay Spring Boot ----------
$env:PATH = "$mavenHome\bin;$env:PATH"

$mvnArgs = @("spring-boot:run")
if ($SkipTests) { $mvnArgs = @("-DskipTests") + $mvnArgs }

Write-Host ""
Write-Host "Dang chay: mvn $($mvnArgs -join ' ')" -ForegroundColor Cyan
& "$mavenBin" @mvnArgs
