import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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
        setBoardInset(2,2);

        controlPoints.add(new Point2D.Double(0,-0));
        controlPoints.add(new Point2D.Double(10,-10));
        controlPoints.add(new Point2D.Double(20,-10));
        controlPoints.add(new Point2D.Double(10,-10));
        controlPoints.add(new Point2D.Double(10,-20));


        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
                updateBounds();
            }
        });
    }

    private void addListeners(){
        SimStage stage = (SimStage) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();

                if (e.getButton() == MouseEvent.BUTTON2) destruct();

                Point2D draggedPoint = null;
                ArrayList<Shape> controlBounds = controlPointBounds();
                for(int i = 0; i < controlPoints.size(); i++){
                    Shape shape = controlBounds.get(i);
                    if(shape.contains(e.getX(),e.getY())) {
                        draggedPoint = controlPoints.get(i);
                        break;
                    }
                }

                draggedIndex = controlPoints.indexOf(draggedPoint);

                if (e.getButton() == MouseEvent.BUTTON3) {
                    if(draggedIndex == -1) return;
                    controlPoints.remove(draggedIndex);
                    draggedIndex = -1;
                    if(controlPoints.size() < 2) destruct();
                    stage.repaint();
                    return;
                }

                if(draggedPoint == null && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    ArrayList<Shape> wireBounds = wireBounds();
                    for(int i = 0; i < wireBounds.size(); i++){
                        Shape shape = wireBounds.get(i);
                        if(shape.contains(e.getX(),e.getY())) {
                            draggedPoint = new Point2D.Double(
                                    e.getX() / SimStage.cellWidth + getBoardLocation().x - boardInsetX,
                                    e.getY() / SimStage.cellWidth + getBoardLocation().y - boardInsetY);
                            controlPoints.add(i + 1, draggedPoint);
                            draggedIndex = i + 1;
                            break;
                        }
                    }
                    repaint();
                    stage.repaint();
                }

                if(draggedPoint == null) return;

                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();

                startX = draggedPoint.getX();
                startY = draggedPoint.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                draggedIndex = -1;
                snapToBoard();
                repaint();
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
                updateBounds();

                repaint();
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
        double cellWidth = SimStage.cellWidth;

        for(int i = 0; i < controlPoints.size() - 1; i++){
            Point2D p1 = controlPoints.get(i);
            Point2D p2 = controlPoints.get(i + 1);
            if(p1.equals(p2)){
                controlPoints.remove(p2);
                if(controlPoints.size() < 2) destruct();
                continue;
            }
            double hyp = p1.distance(p2);

            double offY = Constants.WIRE_BOUND_RADIUS * (p1.getX() - p2.getX()) / hyp;
            double offX = Constants.WIRE_BOUND_RADIUS * (p1.getY() - p2.getY()) / hyp;

            int[] xPoints = new int[4];
            int[] yPoints = new int[4];

            Point board = getBoardLocation();

            xPoints[0] = (int) ((p1.getX() + offX - board.x + boardInsetX) * cellWidth);
            yPoints[0] = (int) ((p1.getY() - offY - board.y + boardInsetY) * cellWidth);

            xPoints[1] = (int) ((p1.getX() - offX - board.x + boardInsetX) * cellWidth);
            yPoints[1] = (int) ((p1.getY() + offY - board.y + boardInsetY) * cellWidth);

            xPoints[2] = (int) ((p2.getX() - offX - board.x + boardInsetX) * cellWidth);
            yPoints[2] = (int) ((p2.getY() + offY - board.y + boardInsetY) * cellWidth);

            xPoints[3] = (int) ((p2.getX() + offX - board.x + boardInsetX) * cellWidth);
            yPoints[3] = (int) ((p2.getY() - offY - board.y + boardInsetY) * cellWidth);

            shapes.add(new Polygon(xPoints, yPoints, 4));
        }
        return shapes;
    }

    public ArrayList<Shape> controlPointBounds(){
        ArrayList<Shape> shapes = new ArrayList<Shape>();
        double cellWidth = SimStage.cellWidth;

        Point board = getBoardLocation();

        for(Point2D p : controlPoints){
            shapes.add(new Ellipse2D.Double(
                    (p.getX() - Constants.WIRE_BOUND_RADIUS - board.x + boardInsetX) * cellWidth,
                    (p.getY() - Constants.WIRE_BOUND_RADIUS - board.y + boardInsetY) * cellWidth,
                    (Constants.WIRE_BOUND_RADIUS * 2.0) * cellWidth,
                    (Constants.WIRE_BOUND_RADIUS * 2.0) * cellWidth));
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

    public void updateBounds(){
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for(Point2D p : controlPoints){
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
        }
        setBoardLocation((int)Math.floor(minX), (int)Math.floor(minY));
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


        return new Dimension((int)Math.ceil(maxX - minX), (int)Math.ceil(maxY - minY));
    }

    @Override
    public void snapToBoard(){
        super.snapToBoard();
        for(Point2D p : controlPoints){
            p.setLocation((int)Math.round(p.getX()), (int)Math.round(p.getY()));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        double cellWidth = SimStage.cellWidth;

        g2.setColor(color);
        g2.setStroke(new BasicStroke((float)(cellWidth * Constants.WIRE_WIDTH)));

        if(isOn) g2.setColor(color);
        else g2.setColor(color.darker());

        Point board = getBoardLocation();
        double remainderX = pixelX - this.getX();
        double remainderY = pixelY - this.getY();

        // draw wire
        double radius = .5;
        ArrayList<Point2D> endPoints = new ArrayList<>();
        for(int i = 0; i < controlPoints.size() - 1; i++){
            Point2D p1 = controlPoints.get(i);
            Point2D p2 = controlPoints.get(i + 1);

            double offX1 = 0, offY1 = 0;
            double offX2 = 0, offY2 = 0;
            if(i > 0) {
                double hyp = p1.distance(p2);
                offX1 = radius * (p1.getX() - p2.getX()) / hyp;
                offY1 = radius * (p1.getY() - p2.getY()) / hyp;
                endPoints.add(new Point2D.Double(
                        (p1.getX() - offX1 - board.x + boardInsetX) * cellWidth + remainderX,
                        (p1.getY() - offY1 - board.y + boardInsetY) * cellWidth + remainderY));

            }
            if(i < controlPoints.size() - 2) {
                double hyp = p1.distance(p2);
                offX2 = radius * (p1.getX() - p2.getX()) / hyp;
                offY2 = radius * (p1.getY() - p2.getY()) / hyp;
                endPoints.add(new Point2D.Double(
                        (p2.getX() + offX2 - board.x + boardInsetX) * cellWidth + remainderX,
                        (p2.getY() + offY2 - board.y + boardInsetY) * cellWidth + remainderY));
            }

            g2.draw( new Line2D.Double(
                    (p1.getX() - offX1 - board.x + boardInsetX) * cellWidth + remainderX,
                    (p1.getY() - offY1 - board.y + boardInsetY) * cellWidth + remainderY,
                    (p2.getX() + offX2 - board.x + boardInsetX) * cellWidth + remainderX,
                    (p2.getY() + offY2 - board.y + boardInsetY) * cellWidth + remainderY));
        }


        //draw wire curves
        for(int i = 0; i < endPoints.size() / 2; i++){
            Point2D p1 = endPoints.get(i * 2);
            Point2D p2 = endPoints.get(i * 2 + 1);

            g2.draw(new QuadCurve2D.Double(
                    p1.getX(), p1.getY(),
                    (controlPoints.get(i + 1).getX() - board.x + boardInsetX) * cellWidth + remainderX,
                    (controlPoints.get(i + 1).getY() - board.y + boardInsetY) * cellWidth + remainderY,
                    p2.getX(), p2.getY()));

        }


        // draw control points
        for(int i = 0; i < controlPoints.size(); i++){
            double terminusRadius = .15;
            if(i == 0 || i == controlPoints.size() - 1) {
                g2.setColor(Constants.PRIMARY_COLOR);
                terminusRadius *= 1.5;

            }
            else if(hasFocus()) g2.setColor(Color.CYAN);
            else continue;
            g2.fill(new Ellipse2D.Double(
                    (controlPoints.get(i).getX() - terminusRadius - board.x + boardInsetX) * cellWidth + remainderX,
                    (controlPoints.get(i).getY() - terminusRadius - board.y + boardInsetY) * cellWidth + remainderY,
                    (terminusRadius * 2.0) * cellWidth,
                    (terminusRadius * 2.0) * cellWidth));
        }


        g2.setColor(hasFocus() ? Color.CYAN : Color.WHITE);
        g2.setStroke(new BasicStroke((float)(cellWidth * .1)));
        for(Shape shape : wireBounds()){
            //2.draw(shape);
        }
        for (Shape shape : controlPointBounds()){
            //g2.draw(shape);
        }
    }
}
