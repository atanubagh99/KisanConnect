# ==============================================================================
# KisanConnect — Development Run Script
# ==============================================================================
# Usage: powershell -ExecutionPolicy Bypass -File run-dev.ps1
# Requires: .env file with API keys (GROQ_API_KEY, HUGGINGFACE_API_KEY, TELEGRAM_BOT_TOKEN)

$ErrorActionPreference = "Continue"

# Load .env file
$envFile = Join-Path $PSScriptRoot ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $key = $matches[1].Trim()
            $val = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($key, $val, "Process")
        }
    }
    Write-Host "Loaded .env file" -ForegroundColor Green
} else {
    Write-Host "WARNING: .env file not found. Set env vars manually." -ForegroundColor Yellow
}

# Set JAVA_HOME if not already set
if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.10'
}

# Run Spring Boot with dev profile
$javaExe = "$env:JAVA_HOME\bin\java.exe"
$baseDir = $PSScriptRoot
$wrapperJar = "$baseDir\.mvn\wrapper\maven-wrapper.jar"

Write-Host "Starting KisanConnect (dev profile)..." -ForegroundColor Cyan
& $javaExe "-Dmaven.multiModuleProjectDirectory=$baseDir" -classpath $wrapperJar org.apache.maven.wrapper.MavenWrapperMain spring-boot:run "-Dspring-boot.run.profiles=dev"
