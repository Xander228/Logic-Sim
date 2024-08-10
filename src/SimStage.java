import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class SimStage extends JPanel {
    double mouseX, mouseY;
    double startX, startY;
    double viewPortOffsetX, viewPortOffsetY;
    double cellWidth;

    private ArrayList<Component> components;
    SimStage() {
        super();
        cellWidth = Constants.DEFAULT_CELL_WIDTH;
        components = new ArrayList<Component>();
        setPreferredSize(new Dimension(Constants.DESIRED_VIEWPORT_WIDTH, Constants.DESIRED_VIEWPORT_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
                if(e.getButton() == MouseEvent.BUTTON1) {
                    mouseX = e.getXOnScreen();
                    mouseY = e.getYOnScreen();

                    startX = viewPortOffsetX;
                    startY = viewPortOffsetY;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double deltaX = (e.getXOnScreen() - mouseX) / cellWidth;
                double deltaY = (e.getYOnScreen() - mouseY) / cellWidth;

                viewPortOffsetX = startX + deltaX;
                viewPortOffsetY = startY + deltaY;

                for(Component c : components) ((LogicComponent) c).updateRelativeLocation();
                repaint();
            }

        });

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                centeredZoom(e.getPreciseWheelRotation());
                for(Component c : components) ((LogicComponent) c).updateRelativeLocation();
            }
        });


        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        ActionMap actionMap = getActionMap();
        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grabFocus();
            }
        });


    }

    public void setTop(Component component) {
        components.remove(component);
        components.addLast(component);
        for(Component c : components){
            add(c, components.size() - components.indexOf(component) - 1);
        }
        repaint();
    }

    public void centeredZoom(double zoomFactor) {
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, this);
        double oldCellWidth = cellWidth;
        cellWidth -= (Constants.ZOOM_SCALE_FACTOR * cellWidth * zoomFactor);
        cellWidth = Math.clamp(cellWidth, Constants.MIN_CELL_WIDTH, Constants.MAX_CELL_WIDTH);
        viewPortOffsetX -= (p.getX() * (cellWidth / oldCellWidth - 1)) / cellWidth;
        viewPortOffsetY -= (p.getY() * (cellWidth / oldCellWidth - 1)) / cellWidth;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double cellBoarderWidth = cellWidth * Constants.CELL_BORDER_RATIO;

        int yMin = (int)Math.floor(-viewPortOffsetY);
        int yMax = (int)Math.ceil((g2d.getClipBounds().getHeight() / cellWidth) - viewPortOffsetY);
        int xMin = (int)Math.floor(-viewPortOffsetX);
        int xMax = (int)Math.ceil((g2d.getClipBounds().getWidth() / cellWidth) - viewPortOffsetX);

        for(int y = yMin; y < yMax; y++) {
            for(int x = xMin; x < xMax; x++) {
                if(x == 0 && y == 0) g2d.setColor(Constants.HOME_COLOR);
                else if(y == 0) g2d.setColor(Constants.X_COLOR);
                else if(x == 0) g2d.setColor(Constants.Y_COLOR);
                else continue;

                ///*
                Rectangle2D rect = new Rectangle2D.Double(
                        (cellBoarderWidth / 2) + (x + viewPortOffsetX) * cellWidth,
                        (cellBoarderWidth / 2) + (y + viewPortOffsetY) * cellWidth,
                        cellWidth - cellBoarderWidth,
                        cellWidth - cellBoarderWidth);
                g2d.fill(rect);
            }
        }
        if(cellBoarderWidth <= .2) return;
        g2d.setColor(Constants.ACCENT_COLOR);
        for(int y = yMin; y < yMax; y++) {
            Rectangle2D rect = new Rectangle2D.Double(
                    0,
                    (-cellBoarderWidth / 2) + (y + viewPortOffsetY) * cellWidth,
                    g2d.getClipBounds().getWidth(),
                    cellBoarderWidth);
            g2d.fill(rect);
        }
        for(int x = xMin; x < xMax; x++) {
            Rectangle2D rect = new Rectangle2D.Double(
                    (-cellBoarderWidth / 2) + (x + viewPortOffsetX) * cellWidth,
                    0,
                    cellBoarderWidth,
                    g2d.getClipBounds().getHeight());
            g2d.fill(rect);
        }

    }
}
