import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Connector{
    double xCenter,yCenter;
    double outerCircleDiameter;

    public Connector() {
    }

    public void setBounds(double x, double y, double diameter) {
        this.xCenter = x;
        this.yCenter = y;
        this.outerCircleDiameter = diameter * 1.8;
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
    }
}
