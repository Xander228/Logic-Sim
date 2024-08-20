import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Connector {
    private double xCenter,yCenter;
    private double radius;
    private double outerCircleDiameter;

    private ConnectorAttributes attributes;

    public Connector(String name, boolean isInput){
        attributes = new ConnectorAttributes(hashCode(), name, isInput);
    }

    public Connector(ConnectorAttributes attributes){
        this.attributes = attributes;
    }

    public void setName(String name){
        attributes.name = name;
    }

    public String getName(){
        return attributes.name;
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
        g2d.setFont(Constants.CONNCETOR_FONT.deriveFont((float)(radius * 1.375)));
        double xOffset = attributes.isInput ? (radius) : -(radius) - (g2d.getFontMetrics().stringWidth(attributes.name));
        g2d.drawString(attributes.name, (float)(xCenter + xOffset), (float)(yCenter + radius / 2.0));
    }
}
