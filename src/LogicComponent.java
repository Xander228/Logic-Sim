import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

public class LogicComponent extends JComponent {
    Color color;
    boolean dragging;
    int boardX, boardY;
    int pixelX, pixelY;

    int mouseX, mouseY;
    double startX, startY;

    double xInset, yInset;

    Connector[] inputConnectors;
    Connector[] outputConnectors;


    LogicComponent(Color color) {

        inputConnectors = new Connector[4];
        outputConnectors = new Connector[4];


        this.color = color;
        setOpaque(false);
        this.dragging = false;
        setLayout(null);

        initializeConnectors();

        InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(KeyStroke.getKeyStroke("UP"),"up");
        inputMap.put(KeyStroke.getKeyStroke('w'),"up");

        inputMap.put(KeyStroke.getKeyStroke("DOWN"),"down");
        inputMap.put(KeyStroke.getKeyStroke('s'),"down");

        inputMap.put(KeyStroke.getKeyStroke("LEFT"),"left");
        inputMap.put(KeyStroke.getKeyStroke('a'),"left");

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"),"right");
        inputMap.put(KeyStroke.getKeyStroke('d'),"right");

        ActionMap actionMap = getActionMap();
        actionMap.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setBoardLocation(getBoardLocation().x, getBoardLocation().y - 1);

            }
        });
        actionMap.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setBoardLocation(getBoardLocation().x, getBoardLocation().y + 1);
            }
        });
        actionMap.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setBoardLocation(getBoardLocation().x - 1, getBoardLocation().y);
            }
        });
        actionMap.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setBoardLocation(getBoardLocation().x + 1, getBoardLocation().y);
            }
        });

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });
    }

    private void initializeConnectors() {
        for (int i = 0; i < inputConnectors.length; i++) {
            inputConnectors[i] = new Connector();
        }

        for (int i = 0; i < outputConnectors.length; i++){
            outputConnectors[i] = new Connector();
        }

    }

    private void addListeners(){
        SimStage stage = (SimStage) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();

                startX = pixelX;
                startY = pixelY;

                dragging = true;
                grabFocus();
                setTop();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                dragging = false;
                snapToBoard();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!dragging) return;

                int eventX = e.getXOnScreen();
                int eventY = e.getYOnScreen();

                int deltaX = eventX - mouseX;
                int deltaY = eventY - mouseY;

                double newX = (int) startX + deltaX;
                double newY = (int) startY + deltaY;

                setPixelLocation(newX, newY);

                stage.repaint();
            }

        });
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                setBorder(new MatteBorder(2,2,2,2,Color.CYAN));
                getParent().repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(null);
                getParent().repaint();
            }
        });
    }

    @Deprecated
    private boolean isOverlapped(){
        ArrayList<Component> components =
                new ArrayList<Component>(Arrays.asList(getParent().getComponents()));
        components.remove(this);

        for(Component component : components)
            if(getBounds().intersects(component.getBounds())) return true;
        return false;
    }
    @Deprecated
    private boolean isOverlapped(int x, int y){
        ArrayList<Component> components =
                new ArrayList<Component>(Arrays.asList(getParent().getComponents()));
        components.remove(this);

        Rectangle bounds = getBounds();
        bounds.setLocation(x,y);

        for(Component component : components)
            if(bounds.intersects(component.getBounds())) return true;
        return false;
    }

    private Point pixelToBoard(Point2D.Double pixel){
        SimStage simStage = (SimStage) getParent();
        return new Point(
                (int) Math.round(pixel.getX() / simStage.cellWidth - simStage.viewPortOffsetX),
                (int) Math.round(pixel.getY() / simStage.cellWidth - simStage.viewPortOffsetY));
    }

    private Point2D.Double boardToPixel(Point board){
        SimStage simStage = (SimStage) getParent();
        return new Point2D.Double(
                (board.getX() + simStage.viewPortOffsetX) * simStage.cellWidth,
                (board.getY() + simStage.viewPortOffsetY) * simStage.cellWidth);
    }

    public void snapToBoard(){
        setBoardLocation(pixelToBoard(new Point2D.Double(pixelX,pixelY)));
    }

    public Point getBoardLocation(){
        return new Point(boardX, boardY);

    }

    public void setBoardLocationFromScreen(Point p){
        SwingUtilities.convertPointFromScreen(p, this.getParent());
        setBoardLocation(pixelToBoard(new Point2D.Double(p.x, p.y)));
    }

    public void setBoardLocation(Point p){
        setBoardLocation(p.x, p.y);
    }

    public void setPixelLocation(Point2D.Double p){
        setPixelLocation(p.x, p.y);
    }

    public void setBoardLocation(int x, int y) {
        boardX = x;
        boardY = y;

        updateRelativeLocation();
    }

    public void setPixelLocation(double x, double y){
        pixelX = (int) x;
        pixelY = (int) y;

        updateLocation();
        repaint();
        getParent().repaint();
    }

    public void updateRelativeLocation(){
        setPixelLocation(boardToPixel(new Point(boardX,boardY)));
    }

    private void updateLocation(){
        SimStage simStage = (SimStage) getParent();
        double cellWidth = simStage.cellWidth;

        xInset = cellWidth ;
        yInset = cellWidth * .1;

        int minHeightInCells = inputConnectors.length;
        minHeightInCells = Math.max(minHeightInCells, outputConnectors.length);
        double pixelHeight = minHeightInCells * cellWidth * 2;

        setBounds(
                (int) (pixelX),
                (int) (pixelY),
                (int) (Constants.DEFAULT_COMPONENT_WIDTH * cellWidth + xInset * 2),
                (int) pixelHeight);

        for (int i = 0; i < inputConnectors.length; i++) {
            inputConnectors[i].setBounds((int) xInset,(int) (cellWidth * (i * 2 + 1) ), cellWidth);
        }

        for (int i = 0; i < outputConnectors.length; i++){
            outputConnectors[i].setBounds((int) (this.getWidth() - xInset),(int) (cellWidth * (i * 2 + 1) ), (int) cellWidth);
        }

        repaint();
    }

    public void setTop(){
        SimStage simStage = (SimStage) getParent();
        simStage.setTop(this);
        grabFocus();
    }

    private void destruct(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        if(pane != null) {
            pane.remove(this);
            pane.revalidate();
            pane.repaint();
        }

    }





    public void createChild(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        Point p = getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(p, pane);

        LogicSelectable logicSelectable = new LogicSelectable(true,color);
        pane.add(logicSelectable);
        pane.setLayer(logicSelectable, JLayeredPane.DRAG_LAYER, 0);

        logicSelectable.revalidate();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                logicSelectable.setStartingLocation(p);
            }
        });

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(color);

        Shape body = new Rectangle2D.Double(
                xInset,
                yInset,
                getWidth() - 2 * xInset,
                getHeight() - 2 * yInset);

        g2d.fill(body);

        for(Connector connector : inputConnectors) connector.paintComponent(g2d);
        for(Connector connector : outputConnectors) connector.paintComponent(g2d);

    }

}
