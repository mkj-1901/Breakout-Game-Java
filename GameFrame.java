import javax.swing.*;
import java.awt.*;
import java.util.List;


// GameFrame is the main window of the application.
// It holds the control panel (with buttons) and the GamePanel (where the game is played).
public class GameFrame extends JFrame {

    private GamePanel gamePanel;
    private DatabaseManager dbManager;
    private JButton pausePlayButton;
    private JLabel scoreLabel;
    private JLabel timeLabel;

    public GameFrame() {
        // Initialize the database manager
        try {
            dbManager = new DatabaseManager();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to database. High scores will not be available.\n" +
                            "Error: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            dbManager = null;
        }

        // --- Create the main game panel ---
        gamePanel = new GamePanel(this);

        // --- Create the top control panel ---
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.DARK_GRAY);

        // Create logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.DARK_GRAY);
        try {
            ImageIcon appIcon = new ImageIcon("resources/breakout-icon.png"); // use PNG for safety
            Image scaled = appIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            this.setIconImage(scaled);
        } catch (Exception e) {
            System.err.println("Failed to load app icon: " + e.getMessage());
        }

        // Create buttons panel with score and time
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setBackground(Color.DARK_GRAY);

        // Left panel for score
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setBackground(Color.DARK_GRAY);
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.YELLOW);
        scoreLabel.setPreferredSize(new Dimension(120, 35));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        leftPanel.add(scoreLabel);

        // Center panel for buttons
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        centerPanel.setBackground(Color.DARK_GRAY);

        // Pause/Play Button
        pausePlayButton = new JButton("Pause");
        pausePlayButton.setFocusable(false);
        pausePlayButton.addActionListener(e -> togglePause());

        // High Scores Button
        JButton highScoresButton = new JButton("High Scores");
        highScoresButton.setFocusable(false);
        highScoresButton.addActionListener(e -> showHighScores());

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.setFocusable(false);
        exitButton.addActionListener(e -> System.exit(0));

        centerPanel.add(pausePlayButton);
        centerPanel.add(highScoresButton);
        centerPanel.add(exitButton);

        // Right panel for time
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(Color.DARK_GRAY);
        timeLabel = new JLabel("Time: 00:00:00");
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timeLabel.setOpaque(true);
        timeLabel.setBackground(Color.YELLOW);
        timeLabel.setPreferredSize(new Dimension(120, 35));
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        rightPanel.add(timeLabel);

        buttonsPanel.add(leftPanel, BorderLayout.WEST);
        buttonsPanel.add(centerPanel, BorderLayout.CENTER);
        buttonsPanel.add(rightPanel, BorderLayout.EAST);

        controlPanel.add(logoPanel, BorderLayout.NORTH);
        controlPanel.add(buttonsPanel, BorderLayout.CENTER);

        // --- Configure the JFrame ---
        this.setLayout(new BorderLayout());
        this.add(controlPanel, BorderLayout.NORTH); // Add control panel to the top
        this.add(gamePanel, BorderLayout.CENTER); // Add game panel to the center

        this.setTitle("Breakout⚽");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(null);

        // Set the window icon to breakout-icon.png
        try {
            Image iconImage = Toolkit.getDefaultToolkit().getImage("resources/breakout-icon.png");
            this.setIconImage(iconImage);
        } catch (Exception e) {
            System.err.println("Failed to load icon image: " + e.getMessage());
        }

        this.setVisible(true);

        // Start a timer to update UI labels
        Timer uiUpdateTimer = new Timer(100, e -> updateUI());
        uiUpdateTimer.start();
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + gamePanel.getScore());
        timeLabel.setText("Time: " + gamePanel.getFormattedTime());
    }

    // Toggles the game between paused and playing states.
    public void togglePause() {
        gamePanel.togglePause();
        if (gamePanel.isPaused()) {
            pausePlayButton.setText("Play");
        } else {
            pausePlayButton.setText("Pause");
        }
    }

    // Fetches and displays the high scores in a dialog box.
    private void showHighScores() {
        if (dbManager == null) {
            JOptionPane.showMessageDialog(this,
                    "Database connection is not available.",
                    "High Scores",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // Pause the game if it's running
            if (!gamePanel.isPaused()) {
                togglePause();
            }

            List<String> scores = dbManager.getHighScores(10);
            if (scores.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No high scores recorded yet!",
                        "High Scores",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder scoreText = new StringBuilder("--- HIGH SCORES ---\n\n");
                for (String score : scores) {
                    scoreText.append(score).append("\n");
                }
                JOptionPane.showMessageDialog(this,
                        scoreText.toString(),
                        "High Scores",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not retrieve high scores: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }


}
