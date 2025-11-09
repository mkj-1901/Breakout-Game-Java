import java.awt.*;

public class Brick {

    private int x, y, width, height;
    private boolean isVisible;
    private Color color;

    public Brick(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isVisible = true;
        this.color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    public void draw(Graphics2D g2d) {
        if (isVisible) {
            g2d.setColor(color);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}