import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LogicComponent extends JComponent {
    Color color;
    boolean draggable;
    int mouseX, mouseY;
    int startingX, startingY;


    LogicComponent(Boolean draggable, Color color) {
        setPreferredSize(new Dimension(100,40));
        setMaximumSize(new Dimension(100,40));
        setBorder(new MatteBorder(1,1,1,1,Color.ORANGE));
        this.color = color;
        this.draggable = draggable;
        Point p = MouseInfo.getPointerInfo().getLocation();
        mouseX = p.x;
        mouseY = p.y;

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });
    }

    private void addListeners(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
         pane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(!draggable) createChild();
            }
             @Override
             public void mouseReleased(MouseEvent e) {
                 if(draggable) destruct();
             }
         });
        pane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!draggable) return;
                int eventX = e.getXOnScreen();
                int eventY = e.getYOnScreen();
                if(!isHovered(eventX, eventY)) return;
                int deltaX = eventX - mouseX;
                int deltaY = eventY - mouseY;
                setLocation(startingX + deltaX, startingY + deltaY);
            }

        });
    }


    public void setStartingLocation(Point p){
        startingX = p.x;
        startingY = p.y;
        setLocation(startingX,startingY);
    }

    public void destruct(){
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

        LogicComponent logicComponent = new LogicComponent(true,Color.BLUE);
        pane.add(logicComponent);
        pane.setLayer(logicComponent, JLayeredPane.DRAG_LAYER, 0);

        logicComponent.revalidate();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                logicComponent.setStartingLocation(p);
            }
        });

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());

    }
}