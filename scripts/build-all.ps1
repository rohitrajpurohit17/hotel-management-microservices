Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Push-Location (Join-Path $PSScriptRoot "..")
try {
    mvn clean package -DskipTests
} finally {
    Pop-Location
}

