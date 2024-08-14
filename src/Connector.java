import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Connector{
    boolean isInput;
    double xCenter,yCenter;
    double radius;
    double outerCircleDiameter;
    String name;
    Font font;

    public Connector(String name, boolean isInput){
        this.name = name;
        this.isInput = isInput;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setBounds(double x, double y, double radius) {
        this.xCenter = x;
        this.yCenter = y;
        this.radius = radius;
        this.outerCircleDiameter = radius * 1.8;
    }

    public void paintComponent(Graphics2D g2d) {

        g2d.setColor(Constants.PRIMARY_COLOR);
        Shape outerCircle = new Ellipse2D.Double(
                xCenter - outerCircleDiameter / 2.0,
                yCenter - outerCircleDiameter / 2.0,
                outerCircleDiameter,
                outerCircleDiameter);
        Shape innerCircle = new Ellipse2D.Double(
                xCenter - outerCircleDiameter / 4.0,
                yCenter - outerCircleDiameter / 4.0,
                outerCircleDiameter / 2.0,
                outerCircleDiameter / 2.0);
        g2d.fill(outerCircle);
        g2d.setColor(Constants.BACKGROUND_COLOR);
        g2d.fill(innerCircle);

        g2d.setColor(Constants.BACKGROUND_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 12).deriveFont((float)(radius * 1.375)));
        double xOffset = isInput ? (radius) : -(radius) - (g2d.getFontMetrics().stringWidth(name));
        g2d.drawString(name, (float)(xCenter + xOffset), (float)(yCenter + radius / 2.0));
    }
}
