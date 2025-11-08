import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("USER");
    private static final String PASS = dotenv.get("PASS");

    private Connection conn;

    public DatabaseManager() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Create the table if it doesn't exist
            createHighScoresTable();
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Make sure to add the .jar to your classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database. Check your URL, username, and password.", e);
        }
    }

    private void createHighScoresTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS highscores (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "score INT NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public void addHighScore(String name, int score) throws SQLException {
        String insertSQL = "INSERT INTO highscores (name, score) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        }
    }

    public List<String> getHighScores(int limit) throws SQLException {
        List<String> scores = new ArrayList<>();
        String selectSQL = "SELECT name, score FROM highscores ORDER BY score DESC LIMIT ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    String name = rs.getString("name");
                    int score = rs.getInt("score");
                    scores.add(String.format("%d. %s - %d", rank++, name, score));
                }
            }
        }
        return scores;
    }
}