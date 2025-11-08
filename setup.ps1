<#
Breakout Game Setup Script
--------------------------
This PowerShell script automatically downloads all dependencies,
extracts MySQL Connector/J, compiles your Java files, and runs the game.
#>

# --- CONFIGURATION ---
$mysqlUrl = "https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-9.5.0.tar.gz"
$dotenvUrl = "https://repo1.maven.org/maven2/io/github/cdimascio/java-dotenv/5.2.2/java-dotenv-5.2.2.jar"
$kotlinUrl = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.9.10/kotlin-stdlib-1.9.10.jar"

$libsDir = "libs"

Write-Host "🎮 Breakout Game Setup Script Starting..." -ForegroundColor Cyan

# --- Step 1: Create libs folder ---
if (!(Test-Path $libsDir)) {
    New-Item -ItemType Directory -Path $libsDir | Out-Null
    Write-Host "📁 Created libs directory."
}

Set-Location $libsDir

# --- Step 2: Download dependencies ---
Write-Host "⬇️ Downloading required JARs..."

Invoke-WebRequest -Uri $mysqlUrl -OutFile "mysql-connector-j-9.5.0.tar.gz"
Invoke-WebRequest -Uri $dotenvUrl -OutFile "java-dotenv-5.2.2.jar"
Invoke-WebRequest -Uri $kotlinUrl -OutFile "kotlin-stdlib-1.9.10.jar"

Write-Host "✅ Downloads complete."

# --- Step 3: Extract MySQL Connector/J ---
Write-Host "📦 Extracting MySQL Connector/J..."
tar -xzf "mysql-connector-j-9.5.0.tar.gz"
Copy-Item "mysql-connector-j-9.5.0\mysql-connector-j-9.5.0.jar" .
Write-Host "✅ MySQL Connector extracted."

# --- Step 4: Return to project root ---
Set-Location ..

# --- Step 5: Compile Java source files ---
Write-Host "🧱 Compiling Java source files..."
$classpath = ".;libs\mysql-connector-j-9.5.0.jar;libs\java-dotenv-5.2.2.jar;libs\kotlin-stdlib-1.9.10.jar"

javac -cp $classpath *.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed. Please check errors above." -ForegroundColor Red
    exit 1
}

Write-Host "✅ Compilation successful."

# --- Step 6: Run the game ---
Write-Host "🚀 Launching Breakout Game..."
java -cp $classpath Main
