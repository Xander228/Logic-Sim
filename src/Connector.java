import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Connector  {

    int x,y;
    public Connector(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Constants.PRIMARY_COLOR);
        Shape outerCircle = new Ellipse2D.Double(x,y,20,20);
        Shape innerCircle = new Ellipse2D.Double(x + 5,y + 5,10,10);
        g2d.fill(outerCircle);
        g2d.setColor(Constants.BACKGROUND_COLOR);
        g2d.fill(innerCircle);
    }
}
