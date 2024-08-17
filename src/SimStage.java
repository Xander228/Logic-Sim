import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class SimStage extends JPanel {
    int oldHeight;
    double mouseX, mouseY;
    double startX, startY;
    double viewPortOffsetX, viewPortOffsetY;
    double cellWidth;

    boolean dragging;

    SimStage() {
        super();

        dragging = false;
        cellWidth = Constants.DEFAULT_CELL_WIDTH;
        setPreferredSize(new Dimension(Constants.DESIRED_VIEWPORT_WIDTH, Constants.DESIRED_VIEWPORT_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");

        inputMap.put(KeyStroke.getKeyStroke("UP"),"up");
        inputMap.put(KeyStroke.getKeyStroke('w'),"up");

        inputMap.put(KeyStroke.getKeyStroke("DOWN"),"down");
        inputMap.put(KeyStroke.getKeyStroke('s'),"down");

        inputMap.put(KeyStroke.getKeyStroke("LEFT"),"left");
        inputMap.put(KeyStroke.getKeyStroke('a'),"left");

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"),"right");
        inputMap.put(KeyStroke.getKeyStroke('d'),"right");



        ActionMap actionMap = getActionMap();
        actionMap.put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grabFocus();
            }
        });

        actionMap.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                viewPortOffsetY += Constants.PAN_SPEED_FACTOR / cellWidth;
                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();
                repaint();
            }
        });
        actionMap.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                viewPortOffsetY -= Constants.PAN_SPEED_FACTOR / cellWidth;
                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();
                repaint();
            }
        });
        actionMap.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                viewPortOffsetX += Constants.PAN_SPEED_FACTOR / cellWidth;
                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();
                repaint();
            }
        });
        actionMap.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                viewPortOffsetX -= Constants.PAN_SPEED_FACTOR / cellWidth;
                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();
                repaint();
            }
        });


    }

    private void addListeners(){
        oldHeight = getHeight();
        viewPortOffsetY = getHeight() / cellWidth - 1;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
                if(e.getButton() != MouseEvent.BUTTON1) return;
                dragging = true;

                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();

                startX = viewPortOffsetX;
                startY = viewPortOffsetY;

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!dragging) return;
                double deltaX = (e.getXOnScreen() - mouseX) / cellWidth;
                double deltaY = (e.getYOnScreen() - mouseY) / cellWidth;

                viewPortOffsetX = startX + deltaX;
                viewPortOffsetY = startY + deltaY;

                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();
                repaint();
            }

        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                centeredZoom(e.getPreciseWheelRotation());
                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e){
                double deltaHeight = (e.getComponent().getHeight() - oldHeight) / cellWidth;

                viewPortOffsetY = viewPortOffsetY + deltaHeight;
                for(Component c : getComponents()) ((LogicComponent) c).updateRelativeLocation();

                oldHeight = e.getComponent().getHeight();
                repaint();
            }
        });
    }

    public void setTop(Component component) {
        add(component, 0);
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
