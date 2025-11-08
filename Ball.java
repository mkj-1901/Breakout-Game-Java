import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Ball {

    private double x, y;
    final int diameter;
    private double dx = 3.0; // Horizontal velocity
    private double dy = -3.0; // Vertical velocity

    public Ball(int x, int y, int diameter) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int) x, (int) y, diameter, diameter);
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, diameter, diameter);
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void increaseSpeed(double amount) {
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        double newSpeed = currentSpeed + amount;
        double scale = newSpeed / currentSpeed;
        dx *= scale;
        dy *= scale;
    }
}
