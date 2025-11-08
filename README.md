# 🎮 Breakout Game Java

A **classic Breakout game** implemented in Java using **Swing** for the graphical user interface.  
The game features a paddle, ball, bricks, scoring system, timer, pause/play functionality, and high scores stored in a MySQL database.

![Application Screenshot](resources/app_ss.png)

---

## 🚀 Features

- 🧱 **Classic Gameplay** — Control the paddle to bounce the ball and break all bricks.  
- 💯 **Scoring System** — Earn points for each brick destroyed.  
- ⏱️ **Timer** — Track your playtime with a built-in timer.  
- ⏸️ **Pause/Play** — Pause and resume the game anytime.  
- 🏆 **High Scores** — Save and view top scores in a MySQL database.  
- 🎮 **Responsive Controls** — Use keyboard or mouse to control the paddle.

---

## 🧩 Libraries and Dependencies

| Library | Description |
|----------|-------------|
| **Java Swing** | Built-in Java GUI library |
| **MySQL Connector/J (mysql-connector-j-9.5.0.jar)** | JDBC driver for MySQL connectivity |
| **java-dotenv (java-dotenv-5.2.2.jar)** | Loads environment variables from `.env` |
| **Kotlin Standard Library (kotlin-stdlib-1.9.10.jar)** | Required by java-dotenv |

---

## ⚙️ Prerequisites

- **Java** — JDK 8 or higher  
- **MySQL** — Installed and running  
- **Git** — For cloning the repository  
- **PowerShell** — For automated setup (Windows only)

---

## 🧱 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Breakout-Game-Java
```

### 2. Set Up MySQL Database

Run the following SQL commands in your MySQL terminal or MySQL Workbench:

```sql
CREATE DATABASE game_db;
CREATE USER 'gameuser'@'localhost' IDENTIFIED BY 'gamepass';
GRANT ALL PRIVILEGES ON game_db.* TO 'gameuser'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Create .env File

Create a file named `.env` in the root directory of the project and add your database credentials:

```
DB_URL=jdbc:mysql://localhost:3306/game_db
USER=gameuser
PASS=gamepass
```

🧠 **Tip:** Replace the credentials with your actual MySQL username and password if needed.  
Example for MySQL root user:

```
DB_URL=jdbc:mysql://localhost:3306/game_db
USER=root
PASS=your_mysql_password
```

### Automated Setup (Windows)

Run the setup script that downloads dependencies, extracts the MySQL connector, compiles, and launches the game automatically.

#### Step 1 — Allow Script Execution (first time only)

```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

#### Step 2 — Run the Setup Script

```powershell
.\setup.ps1
```

### Manual Setup (Linux / macOS)

If you prefer to set it up manually, use the commands below:

#### Download Dependencies

```bash
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-9.5.0.tar.gz
wget https://repo1.maven.org/maven2/io/github/cdimascio/java-dotenv/5.2.2/java-dotenv-5.2.2.jar
wget https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.9.10/kotlin-stdlib-1.9.10.jar
```

#### Extract MySQL Connector/J

```bash
tar -xzf mysql-connector-j-9.5.0.tar.gz
cp mysql-connector-j-9.5.0/mysql-connector-j-9.5.0.jar .
```

#### Compile and Run

```bash
# Compile
javac -cp "mysql-connector-j-9.5.0.jar:java-dotenv-5.2.2.jar:kotlin-stdlib-1.9.10.jar" *.java

# Run
java -cp ".:mysql-connector-j-9.5.0.jar:java-dotenv-5.2.2.jar:kotlin-stdlib-1.9.10.jar" Main
```

### How to Run (Manually)

#### Compile Java Files

**Windows:**

```cmd
javac -cp "mysql-connector-j-9.5.0.jar;java-dotenv-5.2.2.jar;kotlin-stdlib-1.9.10.jar" *.java
```

**Linux/macOS:**

```bash
javac -cp "mysql-connector-j-9.5.0.jar:java-dotenv-5.2.2.jar:kotlin-stdlib-1.9.10.jar" *.java
```

#### Run the Game

**Windows:**

```cmd
java -cp ".;mysql-connector-j-9.5.0.jar;java-dotenv-5.2.2.jar;kotlin-stdlib-1.9.10.jar" Main
```

**Linux/macOS:**

```bash
java -cp ".:mysql-connector-j-9.5.0.jar:java-dotenv-5.2.2.jar:kotlin-stdlib-1.9.10.jar" Main
```

🪟 **Note:** Use semicolons (`;`) on Windows and colons (`:`) on Unix-based systems.

---

## 🧠 Additional Notes

The highscores table is created automatically if it doesn’t exist.

If the database connection fails, the game still runs (high scores disabled).

Use the Spacebar or the Pause Button to pause/resume.

Controls:

⬅️ Left Arrow — Move paddle left

➡️ Right Arrow — Move paddle right

🖱️ Mouse — Move paddle dynamically

The game window is resizable and adapts automatically.
