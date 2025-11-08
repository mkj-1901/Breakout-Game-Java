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
    private enum GameState { PLAYING, PAUSED, GAME_OVER }
    private GameState gameState;
    private Timer gameLoopTimer;
    private GameFrame gameFrame; // Reference to parent frame for DB access

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

    public GamePanel(GameFrame frame) {
        this.gameFrame = frame;
        this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true); // Crucial for KeyListener
        this.addKeyListener(new GameKeyAdapter());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePause();
            }
        });

        initGame();
    }

    /**
     * Initializes all game objects and resets the state.
     */
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
        pausedTime = startTime; // Initialize pausedTime to startTime for correct timer calculation
        elapsedTime = 0;
        totalPausedDuration = 0;

        gameState = GameState.PAUSED;

        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
        gameLoopTimer = new Timer(16, this); // ~60 FPS
        // Do not start the timer here; game starts paused
    }

    /**
     * Main game loop, called by the Timer.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            updateGame();
            repaint();
        }
    }

    /**
     * Main painting method.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw game objects
        paddle.draw(g2d);
        ball.draw(g2d);

        for (Brick brick : bricks) {
            brick.draw(g2d);
        }

        // Draw UI (Score and Time)
        drawUI(g2d);

        // Draw Paused/Game Over messages
        if (gameState == GameState.PAUSED) {
            drawMessage(g2d, "PAUSED");
        } else if (gameState == GameState.GAME_OVER) {
            drawMessage(g2d, "Game Over !!");
        }
    }

    /**
     * Updates the state of all game objects and checks for collisions.
     */
    private void updateGame() {
        paddle.move(PANEL_WIDTH);
        ball.move();
        checkCollisions();
        
        // Update timer
        elapsedTime = System.currentTimeMillis() - startTime - totalPausedDuration;
    }

    /**
     * Central hub for all collision detection logic.
     */
    private void checkCollisions() {
        // Ball with walls
        if (ball.getX() <= 0 || ball.getX() >= PANEL_WIDTH - ball.diameter) {
            ball.reverseX();
        }
        if (ball.getY() <= 0) {
            ball.reverseY();
        }

        // Ball with bottom (Game Over)
        if (ball.getY() >= PANEL_HEIGHT) {
            handleGameOver();
        }

        // Ball with paddle
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.setY(paddle.getY() - ball.diameter); // Prevent sticking
            ball.reverseY();
            ball.increaseSpeed(0.1);
            // Optional: Change ball angle based on where it hits the paddle
            // ...
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
    }

    private void saveScore(String name) {
        if (name != null && !name.trim().isEmpty()) {
            DatabaseManager db = gameFrame.getDatabaseManager();
            if (db != null) {
                try {
                    db.addHighScore(name.trim(), score);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Could not save high score: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Draws the Score and Timer on the screen.
     */
    private void drawUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("Time: " + formatTime(elapsedTime), PANEL_WIDTH - 150, 30);
    }

    /**
     * Draws a centered message (e.g., "PAUSED").
     */
    private void drawMessage(Graphics2D g2d, String message) {
        g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black background
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics fm = g2d.getFontMetrics();
        int msgWidth = fm.stringWidth(message);
        g2d.drawString(message, (PANEL_WIDTH - msgWidth) / 2, PANEL_HEIGHT / 2);
    }

    /**
     * Formats milliseconds into a MM:SS:mmm string.
     */
    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        long milliseconds = (millis % 1000) / 10; // Get 2-digit millis
        return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds);
    }

    /**
     * Toggles the game's paused state.
     */
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

    /**
     * Inner class to handle keyboard input.
     */
    private class GameKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState == GameState.PLAYING) {
                paddle.keyPressed(e);
            }
            
            // Allow pause key (space bar) even if game is over
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameFrame.togglePause();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }
    }
}