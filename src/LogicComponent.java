import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LogicComponent extends LogicBase {
    LogicAttributes attributes;

    private boolean dragging;
    private int mouseX, mouseY;
    private double startX, startY;

    public ArrayList<Connector> inputConnectors;
    public ArrayList<Connector> outputConnectors;

    LogicComponent(LogicAttributes attributes) {
        this.attributes = attributes;
        this.dragging = false;
        setBoardInset(1,0);
        initializeConnectors();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
                registerConnectors();
            }
        });
    }

    private void addListeners(){
        SimStage stage = (SimStage) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) createChild();
                if (e.getButton() == MouseEvent.BUTTON2) destruct();
                if(e.getButton() != MouseEvent.BUTTON1) return;
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();

                Point2D.Double pixel = getPixelLocation();
                startX = pixel.x;
                startY = pixel.y;

                dragging = true;
                grabFocus();
                setTop();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                dragging = false;
                snapToBoard();
                stage.repaint();
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

    private void registerConnectors() {
        SimStage stage = (SimStage) getParent();
        stage.registerConnectors(inputConnectors);
        stage.registerConnectors(outputConnectors);
    }

    private void unRegisterConnectors() {
        SimStage stage = (SimStage) getParent();
        stage.unRegisterConnectors(inputConnectors);
        stage.unRegisterConnectors(outputConnectors);
        for(Connector connector : inputConnectors) if(connector != null) connector.removeAllConnections();
        for(Connector connector : outputConnectors) if(connector != null) connector.removeAllConnections();
    }

    @Override
    protected Dimension updateDimensions() {
        return new Dimension(
                (int) Math.ceil(LogicDisplayController.calculateWidth(this,attributes)),
                Math.max(inputConnectors.size(), outputConnectors.size()) * 2);

    }

    @Override
    protected void updateLocation() {
        super.updateLocation();

        Dimension bounds = updateDimensions();
        double cellWidth = SimStage.cellWidth;
        double doubleWidth = bounds.width * cellWidth;

        boolean isEven = ((bounds.height / 2) - inputConnectors.size()) % 2 == 0;
        for (int i = 0; i < inputConnectors.size(); i++) {
            if(inputConnectors.get(i) == null) continue;
            inputConnectors.get(i).setBounds(
                    super.boardX,
                    super.boardY + i * 2 + (isEven ? 1 : 2),
                    boardInsetX * cellWidth,
                    cellWidth * (i * 2 + (isEven ? 1 : 2)),
                    cellWidth);
        }

        isEven = ((bounds.height / 2) - outputConnectors.size()) % 2 == 0;
        for (int i = 0; i < outputConnectors.size(); i++){
            if(outputConnectors.get(i) == null) continue;
            outputConnectors.get(i).setBounds(
                    super.boardX + bounds.width,
                    super.boardY + i * 2 + (isEven ? 1 : 2),
                    doubleWidth + boardInsetX * cellWidth,
                    cellWidth * (i * 2 + (isEven ? 1 : 2)),
                    cellWidth);
        }
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
    public boolean contains(int x, int y) {
        for (Connector connector : inputConnectors) {
            if (connector != null && connector.isConnected() && connector.contains(x, y)) return false;
        }
        for (Connector connector : outputConnectors) {
            if (connector != null && connector.isConnected() && connector.contains(x, y)) return false;
        }

        return super.contains(x,y);
    }

    @Override
    protected void destruct() {
        unRegisterConnectors();
        super.destruct();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        LogicDisplayController.paint(g, this, inputConnectors, outputConnectors, attributes);
    }
}
