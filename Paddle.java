import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

public class Paddle {

    private int x, y;
    private final int width, height;
    private final int speed = 15;
    private int dx = 0; // 0 = not moving, -1 = left, 1 = right

    public Paddle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(int panelWidth) {
        x += dx * speed;
        if (x <= 0) {
            x = 0;
        }
        if (x >= panelWidth - width) {
            x = panelWidth - width;
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            dx = -1;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            dx = 1;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && dx == -1) {
            dx = 0;
        }
        if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && dx == 1) {
            dx = 0;
        }
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public int getY() {
        return y;
    }
}