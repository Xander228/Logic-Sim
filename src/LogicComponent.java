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

    private double xInset, yInset;
    private double doubleWidth, doubleHeight;

    private double maxInputWidth;
    private double maxOutputWidth;


    LogicComponent(LogicAttributes attributes) {
        this.attributes = attributes;

        this.inputConnectors = new ArrayList<>();
        this.outputConnectors = new ArrayList<>();
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
        for (ConnectorAttributes connectorAttribute : attributes.inputAttributes) {
            if(connectorAttribute == null) inputConnectors.add(null);
            else inputConnectors.add(new Connector(connectorAttribute));
        }

        for (ConnectorAttributes connectorAttribute : attributes.outputAttributes) {
            if(connectorAttribute == null) outputConnectors.add(null);
            else outputConnectors.add(new Connector(connectorAttribute));
        }
    }

    private String[] calculateDrawableName(){
        if(!attributes.verticalName) return attributes.name.split(" ");
        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 12).deriveFont(13.75f));
        int minHeightInCells = Math.max(inputConnectors.size(), outputConnectors.size()) * 2;

        ArrayList<String> strings = new ArrayList<String>();
        String name = attributes.name;
        while(g2d.getFontMetrics().stringWidth(name) / 10.0 > minHeightInCells) {
            for (int i = name.length(); i > 0; i--) {
                if (g2d.getFontMetrics().stringWidth(name.substring(0, i)) / 10.0 > minHeightInCells)
                    continue;
                boolean hasSpace = false;
                for (int j = i - 1; j > 0; j--) {
                    if (name.charAt(j) == ' ') {
                        hasSpace = true;
                        strings.add(name.substring(0, j));
                        name = name.substring(j + 1);
                        break;
                    }
                }
                if(!hasSpace) {
                    strings.add(name.substring(0, i));
                    name = name.substring(i);
                    break;
                }
            }
        }
        if(!name.isEmpty()) strings.add(name);
        return strings.toArray(String[]::new);
    }

    private int calculateWidth(){
        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 12).deriveFont(13.75f));

        for (Connector inputConnector : inputConnectors) {
            if (inputConnector == null) continue;
            maxInputWidth = Math.max(maxInputWidth,
                    g2d.getFontMetrics().stringWidth(inputConnector.getName()) / 10.0);
        }

        for (Connector outputConnector : outputConnectors) {
            if (outputConnector == null) continue;
            maxOutputWidth = Math.max(maxOutputWidth,
                    g2d.getFontMetrics().stringWidth(outputConnector.getName()) / 10.0);
        }

        double maxNameWidth = 0;
        for(String string : calculateDrawableName()){
            maxNameWidth = Math.max(maxNameWidth, g2d.getFontMetrics().stringWidth(string) / 10.0);
        }
        double nameWidth = attributes.verticalName ? calculateDrawableName().length : maxNameWidth;

        return (int) Math.ceil(maxInputWidth + maxOutputWidth + 3 + nameWidth);
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



        int minHeightInCells = Math.max(inputConnectors.size(), outputConnectors.size()) * 2;
        doubleHeight = minHeightInCells * cellWidth;
        doubleWidth = calculateWidth() * cellWidth + xInset * 2;

        setBounds(
                (int) (pixelX),
                (int) (pixelY),
                (int) doubleWidth,
                (int) doubleHeight);

        for (int i = 0; i < inputConnectors.size(); i++) {
            if(inputConnectors.get(i) == null) continue;
            inputConnectors.get(i).setBounds( xInset, (cellWidth * (i * 2 + 1) ), cellWidth);
        }

        for (int i = 0; i < outputConnectors.size(); i++){
            if(outputConnectors.get(i) == null) continue;
            outputConnectors.get(i).setBounds((doubleWidth - xInset), (cellWidth * (i * 2 + 1) ), cellWidth);
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
        setTop();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setColor(attributes.color);

        Shape body = new Rectangle2D.Double(
                xInset,
                yInset,
                doubleWidth - 2 * xInset,
                doubleHeight - 2 * yInset);

        g2d.fill(body);

        for(Connector connector : inputConnectors) {
            if(connector == null) continue;
            connector.paintComponent(g2d);
        }
        for(Connector connector : outputConnectors) {
            if(connector == null) continue;
            connector.paintComponent(g2d);
        }


        SimStage simStage = (SimStage) getParent();
        g2d.setFont(new Font("Arial", Font.BOLD, 12).deriveFont((float)(simStage.cellWidth * 1.375)));

        if(attributes.verticalName) {
            AffineTransform originalContext = g2d.getTransform();
            AffineTransform at = g2d.getTransform();
            at.quadrantRotate(1);
            g2d.setTransform(at);

            String[] strings = calculateDrawableName();
            double offset = (calculateWidth() - (maxInputWidth + maxOutputWidth + 2 + strings.length)) / 2.0;
            offset += maxInputWidth + 2;
            for (int i = 0; i < strings.length; i++) {
                double topOffset = (doubleHeight - g2d.getFontMetrics().stringWidth(strings[i])) / 2.0;
                g2d.drawString(strings[i],
                        (float) (topOffset),
                        (float) (-(offset + 1.375 * (strings.length - i - 1)) * simStage.cellWidth));

            }
            g2d.setTransform(originalContext);
        }
        else {
            String[] strings = calculateDrawableName();
            double offset = (doubleHeight - (g2d.getFontMetrics().getHeight() * strings.length)) / 2;
            for (int i = 0; i < strings.length; i++) {
                double sideOffset = (doubleWidth - g2d.getFontMetrics().stringWidth(strings[i])) / 2.0;
                g2d.drawString(strings[i],
                        (float) (sideOffset),
                        (float) (offset + 1.375 * (i + 1) * simStage.cellWidth));
            }
        }
    }
}
