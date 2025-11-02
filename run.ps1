# Quick compile-and-run script for the ToDoList project
# Usage: from project root in PowerShell run: .\run.ps1
# If your PowerShell ExecutionPolicy prevents running scripts, run:
# powershell -ExecutionPolicy Bypass -File .\run.ps1

$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot

Write-Host "Cleaning and creating output folder .\out\classes..."
if (Test-Path .\out\classes) { Remove-Item -Recurse -Force .\out\classes }
New-Item -ItemType Directory -Path .\out\classes | Out-Null

Write-Host "Collecting Java sources..."
$src = Get-ChildItem -Path .\src\main\java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if (-not $src) { Write-Error "No Java sources found under src\main\java"; Pop-Location; exit 1 }

Write-Host "Compiling $($src.Count) source file(s)..."
javac -d .\out\classes $src
if ($LASTEXITCODE -ne 0) { Write-Error "Compilation failed (javac exit code $LASTEXITCODE)"; Pop-Location; exit $LASTEXITCODE }

Write-Host "Running org.sda.todolist.Main..."
java -cp .\out\classes org.sda.todolist.Main

Pop-Location
