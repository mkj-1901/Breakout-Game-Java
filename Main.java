import javax.swing.SwingUtilities;

/**
 * Main class to run the Breakout game.
 * Ensures the JFrame is created on the Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Swing applications should be run on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new GameFrame());
    }
}