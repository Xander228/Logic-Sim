import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Wire extends LogicBase {
    private Connector start;
    private Connector end;
    private boolean isOn;
    private Color color;
    private ArrayList<Point2D> controlPoints;

    private int draggedIndex;
    private int mouseX, mouseY;
    private double startX, startY;

    public Wire(Color color, boolean isOn) {
        this.color = color;
        this.isOn = isOn;
        this.controlPoints = new ArrayList<>();
        setBorder(new MatteBorder(1,1,1,1, Color.WHITE));
        controlPoints.add(new Point(0,0));
        controlPoints.add(new Point(10,10));
        controlPoints.add(new Point(20,10));
        controlPoints.add(new Point(10,30));
        controlPoints.add(new Point(10,20));



        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });
    }

    private void addListeners(){
        SimStage stage = (SimStage) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) destruct();
                if(e.getButton() != MouseEvent.BUTTON1) return;

                Point2D draggedPoint = null;
                for(int i = 0; i < controlPoints.size(); i++){
                    Shape shape = controlPointBounds().get(i);
                    if(shape.contains(e.getX(),e.getY())) {
                        draggedPoint = controlPoints.get(i);
                        break;
                    }
                }

                draggedIndex = controlPoints.indexOf(draggedPoint);
                System.out.println(draggedPoint);
                if(draggedPoint == null) return;

                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();

                startX = draggedPoint.getX();
                startY = draggedPoint.getY();
                grabFocus();
                setTop();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                draggedIndex = -1;
                snapToBoard();
                stage.repaint();
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(draggedIndex == -1) return;

                int eventX = e.getXOnScreen();
                int eventY = e.getYOnScreen();

                double cellWidth = SimStage.cellWidth;

                double deltaX = (eventX - mouseX) / cellWidth;
                double deltaY = (eventY - mouseY) / cellWidth;

                double newX = startX + deltaX;
                double newY = startY + deltaY;

                controlPoints.set(draggedIndex, new Point2D.Double(newX, newY));
                updateLocation();

                stage.repaint();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
                stage.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
                stage.repaint();
            }
        });
    }



    public ArrayList<Shape> wireBounds(){
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        int boundRadius = 1;
        double cellWidth = SimStage.cellWidth;

        for(int i = 0; i < controlPoints.size() - 1; i++){
            Point2D p1 = controlPoints.get(i);
            Point2D p2 = controlPoints.get(i + 1);
            double hyp = p1.distance(p2);

            double offY = boundRadius * (p1.getX() - p2.getX()) / hyp;
            double offX = boundRadius * (p1.getY() - p2.getY()) / hyp;

            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            xPoints[0] = (int) ((p1.getX() + offX + 1) * cellWidth);
            yPoints[0] = (int) ((p1.getY() - offY + 1) * cellWidth);

            xPoints[1] = (int) ((p1.getX() - offX + 1) * cellWidth);
            yPoints[1] = (int) ((p1.getY() + offY + 1) * cellWidth);

            xPoints[2] = (int) ((p2.getX() - offX + 1) * cellWidth);
            yPoints[2] = (int) ((p2.getY() + offY + 1) * cellWidth);

            xPoints[3] = (int) ((p2.getX() + offX + 1) * cellWidth);
            yPoints[3] = (int) ((p2.getY() - offY + 1) * cellWidth);

            shapes.add(new Polygon(xPoints, yPoints, 4));
        }
        return shapes;
    }

    public ArrayList<Shape> controlPointBounds(){
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        int boundRadius = 1;
        double cellWidth = SimStage.cellWidth;

        for(Point2D p : controlPoints){
            shapes.add(new Ellipse2D.Double(
                    (p.getX() - boundRadius + 1) * cellWidth,
                    (p.getY() - boundRadius + 1) * cellWidth,
                    (boundRadius * 2.0) * cellWidth,
                    (boundRadius * 2.0) * cellWidth));
        }
        return shapes;
    }

    @Override
    public boolean contains(int x, int y) {
        for(Shape shape : wireBounds()){
            if(shape.contains(x,y)) return true;
        }
        for(Shape shape : controlPointBounds()){
            if(shape.contains(x,y)) return true;
        }
        return false;
    }


    @Override
    protected Dimension updateDimensions() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for(Point2D p : controlPoints){
            minX = Math.min(minX, p.getX());
            maxX = Math.max(maxX, p.getX());
            minY = Math.min(minY, p.getY());
            maxY = Math.max(maxY, p.getY());
        }


        return new Dimension((int)(maxX - minX) + 2, (int)(maxY - minY) + 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        double cellWidth = SimStage.cellWidth;

        g2.setColor(color);
        g2.setStroke(new BasicStroke((float)(cellWidth * .2)));

        if(isOn) g2.setColor(color);
        else g2.setColor(color.darker());


        for(int i = 0; i < controlPoints.size() - 1; i++){
            g2.draw( new Line2D.Double(
                    (controlPoints.get(i).getX() + 1) * cellWidth,
                    (controlPoints.get(i).getY() + 1) * cellWidth,
                    (controlPoints.get(i + 1).getX() + 1) * cellWidth,
                    (controlPoints.get(i + 1).getY() + 1) * cellWidth));
        }

        g2.setColor(Constants.PRIMARY_COLOR);
        Shape terminus = new Ellipse2D.Double(
                (controlPoints.getFirst().getX() - Constants.WIRE_WIDTH + 1) * cellWidth,
                (controlPoints.getFirst().getY() - Constants.WIRE_WIDTH + 1) * cellWidth,
                (Constants.WIRE_WIDTH * 2.0) * cellWidth,
                (Constants.WIRE_WIDTH * 2.0) * cellWidth);
        g2.fill(terminus);

        terminus = new Ellipse2D.Double(
                (controlPoints.getLast().getX() - Constants.WIRE_WIDTH + 1) * cellWidth,
                (controlPoints.getLast().getY() - Constants.WIRE_WIDTH + 1) * cellWidth,
                (Constants.WIRE_WIDTH * 2.0) * cellWidth,
                (Constants.WIRE_WIDTH * 2.0) * cellWidth);
        g2.fill(terminus);

        g2.setColor(hasFocus() ? Color.CYAN : Color.WHITE);
        g2.setStroke(new BasicStroke((float)(cellWidth * .1)));
        for(Shape shape : wireBounds()){
            g2.draw(shape);
        }
        for (Shape shape : controlPointBounds()){
            g2.draw(shape);
        }
    }
}
