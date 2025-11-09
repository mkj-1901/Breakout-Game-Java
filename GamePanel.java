// Unused imports for blur filter have been removed
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {

    // --- Constants ---
    static final int PANEL_WIDTH = 800;
    static final int PANEL_HEIGHT = 600;
    static final int PADDLE_WIDTH = 100;
    static final int PADDLE_HEIGHT = 20;
    static final int BALL_DIAMETER = 20;
    static final int BRICK_ROWS = 5;
    static final int BRICK_COLS = 10;
    static final int BRICK_WIDTH = 70;
    static final int BRICK_HEIGHT = 30;
    static final int BRICK_GAP = 5;

    // --- Game State ---
    private enum GameState {
        PLAYING, PAUSED, GAME_OVER
    }

    private GameState gameState;
    private Timer gameLoopTimer;
    private GameFrame gameFrame;

    // --- Game Objects ---
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    // --- UI & Scoring ---
    private int score;
    private long startTime;
    private long elapsedTime;
    private long pausedTime;
    private long totalPausedDuration;

    public int getScore() {
        return score;
    }

    public String getFormattedTime() {
        return formatTime(elapsedTime);
    }

    public GamePanel(GameFrame frame) {
        this.gameFrame = frame;
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new GameKeyAdapter());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePause();
            }
        });

        initGame();
    }

    // Initializes all game objects and resets the state.
    public void initGame() {
        paddle = new Paddle((PANEL_WIDTH - PADDLE_WIDTH) / 2, PANEL_HEIGHT - 50, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Ball((PANEL_WIDTH - BALL_DIAMETER) / 2, (PANEL_HEIGHT - BALL_DIAMETER) / 2, BALL_DIAMETER);

        bricks = new ArrayList<>();
        int startX = (PANEL_WIDTH - (BRICK_COLS * (BRICK_WIDTH + BRICK_GAP))) / 2;
        int startY = 50;

        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                int x = startX + col * (BRICK_WIDTH + BRICK_GAP);
                int y = startY + row * (BRICK_HEIGHT + BRICK_GAP);
                bricks.add(new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT));
            }
        }

        score = 0;
        startTime = System.currentTimeMillis();
        pausedTime = startTime;
        elapsedTime = 0;
        totalPausedDuration = 0;

        gameState = GameState.PAUSED;

        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
        gameLoopTimer = new Timer(16, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            updateGame();
            repaint();
        }
    }

    // Draws all game objects and UI elements. Handles painting for different game states.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw border around the game area
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(0, 0, PANEL_WIDTH - 1, PANEL_HEIGHT - 1);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(5, 5, PANEL_WIDTH - 10, PANEL_HEIGHT - 10);

        paddle.draw(g2d);
        ball.draw(g2d);
        for (Brick brick : bricks) {
            brick.draw(g2d);
        }

        if (gameState == GameState.PAUSED) {
            g2d.setColor(new Color(128, 128, 128, 150));
            g2d.fillRect(5, 5, PANEL_WIDTH - 10, PANEL_HEIGHT - 10);

            // Draw Paused messages on top
            g2d.setColor(Color.WHITE); // Set text color to be visible
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            drawMessage(g2d, "PAUSED", PANEL_HEIGHT / 2 - 20);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            drawMessage(g2d, "Press Spacebar to Play", PANEL_HEIGHT / 2 + 30);
            drawMessage(g2d, "Use <-arrow-> keys to move paddle", PANEL_HEIGHT / 2 + 50);

        } else if (gameState == GameState.GAME_OVER) {
            // Draw Game Over message
            g2d.setColor(Color.WHITE); // Set text color to be visible
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            drawMessage(g2d, "Game Over !!", PANEL_HEIGHT / 2);
        }
    }

    // Updates the state of all game objects and checks for collisions.
    private void updateGame() {
        paddle.move(PANEL_WIDTH);
        ball.move();
        checkCollisions();

        // Update timer
        elapsedTime = System.currentTimeMillis() - startTime - totalPausedDuration;
    }

    // Central hub for all collision detection logic.
    private void checkCollisions() {
        // Ball with walls (adjusted for border)
        if (ball.getX() <= 5 || ball.getX() >= PANEL_WIDTH - 5 - ball.diameter) {
            ball.reverseX();
        }
        if (ball.getY() <= 5) {
            ball.reverseY();
        }

        // Ball with bottom (Game Over)
        if (ball.getY() >= PANEL_HEIGHT - 5) {
            handleGameOver();
        }
        // Ball with paddle
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.setY(paddle.getY() - ball.diameter); // Prevent sticking
            ball.reverseY();
            ball.increaseSpeed(0.1);
        }

        // Ball with bricks
        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (brick.isVisible() && ball.getBounds().intersects(brick.getBounds())) {
                brick.setVisible(false);
                ball.reverseY(); // Simple bounce
                ball.increaseSpeed(0.1);
                score += 10;

                // Check for win
                if (checkWin()) {
                    handleGameWin();
                }
                break; // A ball can only break one brick per frame
            }
        }
    }

    private boolean checkWin() {
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                return false;
            }
        }
        return true;
    }

    private void handleGameWin() {
        gameState = GameState.GAME_OVER;
        gameLoopTimer.stop();
        String name = JOptionPane.showInputDialog(this,
                "YOU WIN! Final Score: " + score + "\nEnter your name:",
                "Congratulations!",
                JOptionPane.PLAIN_MESSAGE);

        saveScore(name);
        initGame(); // Restart game
        repaint(); // Repaint to show the new "PAUSED" screen
    }

    private void handleGameOver() {
        gameState = GameState.GAME_OVER;
        gameLoopTimer.stop();

        String name = JOptionPane.showInputDialog(this,
                "Game Over! Final Score: " + score + "\nEnter your name:",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);

        saveScore(name);
        initGame(); // Restart game
        repaint(); // Repaint to show the new "PAUSED" screen
    }

    // Saves the score on a background thread to prevent freezing the application (EDT).
    private void saveScore(String name) {
        if (name == null || name.trim().isEmpty()) {
            return; // Don't save if name is invalid
        }

        final String finalName = name.trim();
        final int finalScore = this.score; // Capture the score at this moment
        final Component parent = this; // For showing dialogs

        // Use SwingWorker to run the DB query on a background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Exception error = null;

            @Override
            protected Void doInBackground() throws Exception {
                // This runs on a separate, background thread
                try {
                    DatabaseManager db = gameFrame.getDatabaseManager();
                    if (db != null) {
                        db.addHighScore(finalName, finalScore);
                    }
                } catch (Exception e) {
                    // Store any errors that happen
                    this.error = e;
                }
                return null;
            }

            @Override
            protected void done() {
                // This runs back on the Swing (EDT) thread after doInBackground is finished.
                if (error != null) {
                    // If an error happened, show it now.
                    JOptionPane.showMessageDialog(parent,
                            "Could not save high score: " + error.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }




     // Draws a centered message at a specific y-position.
    private void drawMessage(Graphics2D g2d, String message, int y) {
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(message);
        g2d.drawString(message, (PANEL_WIDTH - msgWidth) / 2, y);
    }



    // Formats milliseconds into a MM:SS:mmm string.
    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long milliseconds = (millis % 1000) / 10; // Get 2-digit millis
        return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds);
    }

    // Toggles the game's paused state.
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            pausedTime = System.currentTimeMillis(); // Record when we paused
            gameLoopTimer.stop();
            repaint(); // Redraw to show "PAUSED" message
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            // Add the duration of the pause to the total pause time
            totalPausedDuration += System.currentTimeMillis() - pausedTime;
            gameLoopTimer.start();
        }
    }

    public boolean isPaused() {
        return gameState == GameState.PAUSED;
    }

    // Inner class to handle keyboard input.
    private class GameKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState == GameState.PLAYING) {
                paddle.keyPressed(e);
            }

            // Allow pause key (space bar) even if game is over
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                togglePause();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }
    }
}