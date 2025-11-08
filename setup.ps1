Write-Host "Breakout Game Setup Script Starting..."

# --- Step 1: Compile Java source files ---
Write-Host "Compiling Java source files..."

# Classpath includes the JARs that already exist in your repo
$classpath = ".;mysql-connector-j-9.5.0.jar;java-dotenv-5.2.2.jar;kotlin-stdlib-1.9.10.jar"

javac -cp $classpath *.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed. Please check errors above."
    exit 1
}

Write-Host "Compilation successful."

# --- Step 2: Run the game ---
Write-Host "Launching Breakout Game..."
java -cp $classpath Main
