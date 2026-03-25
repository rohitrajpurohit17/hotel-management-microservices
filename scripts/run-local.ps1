Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$modules = @(
    "service-discovery",
    "config-server",
    "hotel-service",
    "user-service",
    "inventory-service",
    "payment-service",
    "notification-service",
    "booking-service",
    "api-gateway"
)

foreach ($module in $modules) {
    $command = "Set-Location '$root\\$module'; mvn spring-boot:run"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command | Out-Null
}
