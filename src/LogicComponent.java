import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LogicComponent extends JComponent {
    Color color;
    boolean dragging;
    int mouseX, mouseY;
    int x, y;


    LogicComponent(Color color) {
        setPreferredSize(new Dimension(100,100));
        setBorder(new MatteBorder(2,2,2,2,Color.BLACK));
        this.color = color;
        this.dragging = false;
        setLayout(null);




        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });
    }

    private void addListeners(){
        JPanel stage = (JPanel) this.getParent();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
                dragging = false;
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

                newX = Math.min(stage.getWidth() - getWidth(),
                        Math.max(0, newX));
                newY = Math.min(stage.getHeight() - getHeight(),
                        Math.max(0, newY));
                setLocation(newX, newY);
                stage.repaint();
            }

        });
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                setBorder(new MatteBorder(2,2,2,2,Color.CYAN));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(new MatteBorder(2,2,2,2,Color.BLACK));
            }
        });
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
        //x = x / 10 * 10;
        //y = y / 10 * 10;
        super.setLocation(x, y);
        setBounds(x, y, getPreferredSize().width, getPreferredSize().height);
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

        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());

        Connector connector = new Connector(50,50);
        connector.paintComponent(g);

    }
}
