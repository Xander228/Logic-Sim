import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class LogicComponent extends JComponent {
    Color color;
    boolean dragging;
    int mouseX, mouseY;
    int x, y;
    int xInset, yInset;

    Connector[] inputConnectors;
    Connector[] outputConnectors;


    LogicComponent(Color color) {
        int desiredWidth = 80;
        xInset = Constants.DEFAULT_CELL_WIDTH;
        yInset = 11;

        inputConnectors = new Connector[4];
        outputConnectors = new Connector[4];

        int minHeightInCells = inputConnectors.length + 1;
        minHeightInCells = Math.max(minHeightInCells, outputConnectors.length + 1);
        int pixelHeight = minHeightInCells * Constants.DEFAULT_CELL_WIDTH;

        setPreferredSize(new Dimension(desiredWidth + xInset * 2,pixelHeight));
        setBounds(x, y, getPreferredSize().width, getPreferredSize().height);

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
                setLocation(getLocation().x, getLocation().y - Constants.DEFAULT_CELL_WIDTH);

            }
        });
        actionMap.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setLocation(getLocation().x, getLocation().y + Constants.DEFAULT_CELL_WIDTH);
            }
        });
        actionMap.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setLocation(getLocation().x - Constants.DEFAULT_CELL_WIDTH, getLocation().y);
            }
        });
        actionMap.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setLocation(getLocation().x + Constants.DEFAULT_CELL_WIDTH, getLocation().y);
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
            inputConnectors[i] = new Connector(xInset,Constants.DEFAULT_CELL_WIDTH * (i + 1));
        }

        for (int i = 0; i < outputConnectors.length; i++){
            outputConnectors[i] = new Connector(this.getWidth() - xInset,Constants.DEFAULT_CELL_WIDTH * (i + 1));
        }

    }

    private void addListeners(){
        JPanel stage = (JPanel) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                Point p = e.getLocationOnScreen();
                mouseX = p.x;
                mouseY = p.y;

                x = getX();
                y = getY();

                dragging = true;
                grabFocus();
                setTop();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                dragging = false;
                setLocation(getLocation());
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

                int newX = x + deltaX;
                int newY = y + deltaY;

                setLocation(newX, newY);

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

    private boolean isOverlapped(){
        ArrayList<Component> components =
                new ArrayList<Component>(Arrays.asList(getParent().getComponents()));
        components.remove(this);

        for(Component component : components)
            if(getBounds().intersects(component.getBounds())) return true;
        return false;
    }

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


    public void setLocationFromScreen(Point p){
        SwingUtilities.convertPointFromScreen(p, this.getParent());
        setLocation(p.x, p.y);
    }
    @Override
    public void setLocation(Point p){
        setLocation(p.x, p.y);
    }

    @Override
    public void setLocation(int x, int y) {
        int oldX = getX();
        int oldY = getY();
        if(!dragging) {
            x = (int) Math.round(x / (double) Constants.DEFAULT_CELL_WIDTH) * Constants.DEFAULT_CELL_WIDTH;
            y = (int) Math.round(y / (double) Constants.DEFAULT_CELL_WIDTH) * Constants.DEFAULT_CELL_WIDTH;
        }

        x = Math.min(getParent().getWidth() - getWidth(),
                Math.max(0, x));
        y = Math.min(getParent().getHeight() - getHeight(),
                Math.max(0, y));

        super.setLocation(x, y);

        repaint();
        getParent().repaint();
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
        g2d.fillRect(
                xInset,
                yInset,
                getWidth() - 2 * xInset,
                getHeight() - 2 * yInset);


        for(Connector connector : inputConnectors) connector.paintComponent(g2d);
        for(Connector connector : outputConnectors) connector.paintComponent(g2d);

    }
}
