import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class LogicSelectable extends JComponent {
    LogicAttributes attributes;

    public ArrayList<Connector> inputConnectors;
    public ArrayList<Connector> outputConnectors;

    boolean draggable;
    int mouseX, mouseY;
    int startingX, startingY;
    int pixelX, pixelY;

    MouseAdapter mouseAdapter;
    MouseMotionAdapter mouseMotionAdapter;

    LogicSelectable(Color color) {
        ArrayList<ConnectorAttributes> inputAttributes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            inputAttributes.add(null);
            if(Math.random() > .5) inputAttributes.set(i, new ConnectorAttributes(i,"" + i, true));
        }

        ArrayList<ConnectorAttributes> outputAttributes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            outputAttributes.add(null);
            if(Math.random() > .5) outputAttributes.set(i, new ConnectorAttributes(i,"" + i, false));
        }

        this.attributes =  new LogicAttributes(
                hashCode(),
                "Logic Component",
                Math.random() > .5,
                color,
                inputAttributes,
                outputAttributes);

        this.draggable = false;

        setPreferredSize(new Dimension(100,40));
        setMaximumSize(new Dimension(100,40));
        setBorder(new MatteBorder(2,2,2,2,Color.BLACK));
        Point p = MouseInfo.getPointerInfo().getLocation();
        mouseX = p.x;
        mouseY = p.y;



        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });
    }

    private LogicSelectable(boolean draggable, LogicAttributes attributes) {
        this.attributes = attributes;
        this.draggable = draggable;
        if(draggable) initializeConnectors();

        setPreferredSize(new Dimension(100,40));
        setMaximumSize(new Dimension(100,40));
        if(draggable) setBorder(new MatteBorder(2,2,2,2,Color.CYAN));
        else setBorder(new MatteBorder(2,2,2,2,Color.BLACK));
        Point p = MouseInfo.getPointerInfo().getLocation();
        mouseX = p.x;
        mouseY = p.y;

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
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

    private void addListeners(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                if (!isHovered(e.getLocationOnScreen())) return;
                if (!checkIfSelected()) return;
                if (!draggable) createChild();
                grabFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                if (!draggable) return;
                if(
                        e.getX() > Constants.BORDER_WIDTH + Constants.BUTTON_PANEL_HEIGHT &&
                        e.getX() < pane.getWidth() - (Constants.BORDER_WIDTH * 3 + Constants.BUTTON_PANEL_HEIGHT) &&
                        e.getY() > Constants.BORDER_WIDTH * 5 + Constants.BUTTON_PANEL_HEIGHT * 2 - pane.getHeight() &&
                        e.getY() < -(Constants.BORDER_WIDTH))
                    createComponent();

                destruct();
            }
        };
        mouseMotionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!draggable) return;
                int eventX = e.getXOnScreen();
                int eventY = e.getYOnScreen();

                int deltaX = eventX - mouseX;
                int deltaY = eventY - mouseY;
                pixelX = startingX + deltaX;
                pixelY = startingY + deltaY;
                updateLocation();
                pane.repaint();
            }

        };

        pane.addMouseListener(mouseAdapter);
        pane.addMouseMotionListener(mouseMotionAdapter);
    }

    private boolean checkIfSelected(){
        ComponentSelector componentSelector =
                (ComponentSelector) SwingUtilities.getAncestorOfClass(ComponentSelector.class, this);
        return (componentSelector.checkIfSelected());
    }

    private void setStartingLocation(int x, int y){
        updateLocation();
        startingX = x - getWidth() / 2;
        startingY = y - getHeight() / 2;
        pixelX = startingX;
        pixelY = startingY;
        updateLocation();
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

    private void destruct(){
        Container pane = getParent();
        if(pane == null) return;
        pane.removeMouseListener(mouseAdapter);
        pane.removeMouseMotionListener(mouseMotionAdapter);
        pane.remove(this);
        pane.revalidate();
        pane.repaint();

    }

    private void createComponent(){
        MainContainer mainContainer = (MainContainer) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        if(mainContainer == null) return;

        LogicComponent logicComponent = new LogicComponent(attributes);

        mainContainer.addToBoard(logicComponent);
        logicComponent.setBoardLocationFromScreen(getLocationOnScreen());
        logicComponent.setTop();
    }

    public boolean isHovered(Point p){
        SwingUtilities.convertPointFromScreen(p, this);
        return 0 <= p.getX() && p.getX() <= getWidth() && 0 <= p.getY() && p.getY() <= getHeight();
    }

    public boolean isHovered(int x, int y){
        Point p = new Point(x,y);
        return isHovered(p);
    }


    public void createChild(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, pane);

        LogicSelectable logicSelectable = new LogicSelectable(true, attributes);
        pane.add(logicSelectable);
        logicSelectable.setStartingLocation(p.x, p.y);
        pane.setLayer(logicSelectable, JLayeredPane.DRAG_LAYER, 0);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = LogicDisplayController.getGraphics2D(g);

        if(!draggable){
            g2d.setColor(attributes.color);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        LogicDisplayController.paint(g, this, inputConnectors, outputConnectors, attributes);
    }
}
