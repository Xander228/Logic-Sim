import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class LogicComponent extends JComponent {
    LogicAttributes attributes;

    public ArrayList<Connector> inputConnectors;
    public ArrayList<Connector> outputConnectors;

    private boolean dragging;
    private int boardX, boardY;
    private int pixelX, pixelY;

    private int mouseX, mouseY;
    private double startX, startY;


    LogicComponent(LogicAttributes attributes) {
        this.attributes = attributes;

        initializeConnectors();

        setOpaque(false);
        this.dragging = false;
        setLayout(null);

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

    private void addListeners(){

        SimStage stage = (SimStage) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    createChild();
                }
                if (e.getButton() == MouseEvent.BUTTON2) destruct();
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

    private void initializeConnectors() {
        this.inputConnectors = new ArrayList<>();
        for (ConnectorAttributes connectorAttribute : attributes.inputAttributes) {
            if(connectorAttribute == null) inputConnectors.add(null);
            else inputConnectors.add(new Connector(connectorAttribute));
        }

        this.outputConnectors = new ArrayList<>();
        for (ConnectorAttributes connectorAttribute : attributes.outputAttributes) {
            if(connectorAttribute == null) outputConnectors.add(null);
            else outputConnectors.add(new Connector(connectorAttribute));
        }
    }

    private Point pixelToBoard(Point2D.Double pixel){
        SimStage simStage = (SimStage) getParent();
        return new Point(
                (int) Math.round(pixel.getX() / SimStage.cellWidth - simStage.viewPortOffsetX),
                (int) Math.round(pixel.getY() / SimStage.cellWidth - simStage.viewPortOffsetY));
    }

    private Point2D.Double boardToPixel(Point board){
        SimStage simStage = (SimStage) getParent();
        return new Point2D.Double(
                (board.getX() + simStage.viewPortOffsetX) * SimStage.cellWidth,
                (board.getY() + simStage.viewPortOffsetY) * SimStage.cellWidth);
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
        double cellWidth = SimStage.cellWidth;

        int minHeightInCells = Math.max(inputConnectors.size(), outputConnectors.size()) * 2;
        double doubleHeight = minHeightInCells * cellWidth;
        int minWidthInCells = (int) Math.ceil(LogicDisplayController.calculateWidth(this,attributes)) + 2;
        double doubleWidth = minWidthInCells * cellWidth;

        setBounds(
                (int) (pixelX),
                (int) (pixelY),
                (int) doubleWidth,
                (int) doubleHeight);

        boolean isEven = ((minHeightInCells / 2) - inputConnectors.size()) % 2 == 0;
        for (int i = 0; i < inputConnectors.size(); i++) {
            if(inputConnectors.get(i) == null) continue;
            inputConnectors.get(i).setBounds(cellWidth, (cellWidth * (i * 2 + (isEven ? 1 : 2)) ), cellWidth);
        }

        isEven = ((minHeightInCells / 2) - outputConnectors.size()) % 2 == 0;
        for (int i = 0; i < outputConnectors.size(); i++){
            if(outputConnectors.get(i) == null) continue;
            outputConnectors.get(i).setBounds((doubleWidth - cellWidth), (cellWidth * (i * 2 + (isEven ? 1 : 2)) ), cellWidth);
        }

        repaint();
    }

    public void setTop(){
        SimStage simStage = (SimStage) getParent();
        simStage.setTop(this);
        grabFocus();
    }

    private void destruct(){

        //remove all listeners
        for (MouseListener ml : getMouseListeners()) {
            removeMouseListener(ml);
        }
        for (MouseMotionListener mml : getMouseMotionListeners()) {
            removeMouseMotionListener(mml);
        }
        for (FocusListener fl : getFocusListeners()) {
            removeFocusListener(fl);
        }

        getInputMap(JComponent.WHEN_FOCUSED).clear();
        getActionMap().clear();

        JComponent pane = (JComponent) SwingUtilities.getAncestorOfClass(JComponent.class, this);
        if(pane == null) return;
        pane.remove(this);
        pane.revalidate();
        pane.repaint();
        if(hasFocus()) pane.grabFocus();
    }

    public void createChild() {
        SimStage simStage = (SimStage) SwingUtilities.getAncestorOfClass(SimStage.class, this);
        if(simStage == null) return;
        LogicComponent logicComponent = new LogicComponent(attributes);
        simStage.add(logicComponent);
        logicComponent.setBoardLocationFromScreen(getLocationOnScreen());
        logicComponent.setTop();
        setTop();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        LogicDisplayController.paint(g, this, inputConnectors, outputConnectors, attributes);
    }
}
