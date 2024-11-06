import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Connector {
    private double boardX, boardY;
    private double pixelX, pixelY;
    private double radius;
    private ArrayList<Wire> wires;
    private Color color;

    private ConnectorAttributes attributes;

    public Connector(String name, boolean isInput){
        new Connector(new ConnectorAttributes(hashCode(), name, isInput));
    }

    public Connector(ConnectorAttributes attributes){
        wires = new ArrayList<>();
        this.attributes = attributes;
    }

    public void setName(String name){
        attributes.name = name;
    }

    public String getName(){
        return attributes.name;
    }

    public boolean isInput(){
        return attributes.isInput;
    }

    public boolean isConnected(){
        return !wires.isEmpty();
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    /**
    *@param boardX: the x coordinate of the connector on the board
    *@param boardY: the y coordinate of the connector on the board
    *@param pixelX: the x coordinate of the connector relative to it's parent
    *@param pixelY: the y coordinate of the connector relative to it's parent
    *@param radius: the radius of the connector
    **/
    public void setBounds(double boardX, double boardY, double pixelX, double pixelY, double radius){
        this.boardX = boardX;
        this.boardY = boardY;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.radius = radius;

        for (Wire wire : wires) {
            wire.updateControlPoints();
        }
    }

    /**
     *@param wire: the wire to be added
     *@return true if the wire was added or is already present, false otherwise
     **/
    public boolean addWire(Wire wire){
        if(wires.contains(wire)) return true;
        if(attributes.isInput && !wires.isEmpty()) return false;
        if(wires.isEmpty()) setColor(wire.getColor());
        wires.add(wire);
        return true;
    }

    /**
    *@param wire: the wire to be removed
    **/
    public void removeWire(Wire wire){
        wires.remove(wire);
        if(wires.isEmpty()) setColor(null);
    }

    /**
    * @return the board location of the connector
    **/
    public Point2D.Double getLocation(){
        return new Point2D.Double(boardX, boardY);
    }

    public boolean contains(int x, int y){
        return Math.pow(x - pixelX, 2) + Math.pow(y - pixelY, 2) <= Math.pow(radius, 2);
    }

    public void removeAllConnections(){
        for (Wire wire : wires) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    wire.removeConnector(Connector.this);
                }
            });

        }
    }

    public void paintComponent(Graphics2D g2d) {
        double outerCircleDiameter = radius * 1.5;
        g2d.setColor(Constants.PRIMARY_COLOR);
        Shape outerCircle = new Ellipse2D.Double(
                pixelX - outerCircleDiameter / 2.0,
                pixelY - outerCircleDiameter / 2.0,
                outerCircleDiameter,
                outerCircleDiameter);
        Shape innerCircle = new Ellipse2D.Double(
                pixelX - outerCircleDiameter / 4.0,
                pixelY - outerCircleDiameter / 4.0,
                outerCircleDiameter / 2.0,
                outerCircleDiameter / 2.0);
        g2d.fill(outerCircle);
        g2d.setColor((color == null) ? Constants.BACKGROUND_COLOR : color);
        g2d.fill(innerCircle);

        g2d.setColor(Constants.BACKGROUND_COLOR);
        g2d.setFont(Constants.CONNCETOR_FONT.deriveFont((float)(radius * 1.375)));
        double xOffset = attributes.isInput ? (radius) : -(radius) - (g2d.getFontMetrics().stringWidth(attributes.name));
        g2d.drawString(attributes.name, (float)(pixelX + xOffset), (float)(pixelY + radius / 2.0));
    }
}
