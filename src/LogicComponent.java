import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LogicComponent extends JComponent {
    Color color;
    final boolean draggable;
    int mouseX, mouseY;
    int x,y;
    boolean isNew;

    LogicComponent(Boolean draggable, Color color) {
        isNew = true;
        setPreferredSize(new Dimension(100,40));
        setMaximumSize(new Dimension(100,40));
        setBorder(new MatteBorder(1,1,1,1,Color.ORANGE));
        this.color = color;
        this.draggable = draggable;

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });

    }

    private void addListeners(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        pane.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                int eventX = e.getXOnScreen();
                int eventY = e.getYOnScreen();
                if(!isHovered(eventX, eventY)) return;

                mouseX = eventX;
                mouseY = eventY;

                x = getX();
                y = getY();

                if(!draggable) createChild();
            }
        });

        if (draggable) pane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int eventX = e.getXOnScreen();
                int eventY = e.getYOnScreen();
                if(!isHovered(eventX, eventY)) return;

                int deltaX = eventX - mouseX;
                int deltaY = eventY - mouseY;
                setLocation(x + deltaX, y + deltaY);
            }
        });
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
        LogicComponent logicComponent = new LogicComponent(true,Color.BLUE);
        pane.add(logicComponent);
        pane.setLayer(logicComponent, JLayeredPane.DRAG_LAYER, 0);
        Point p = getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(p, pane);
        logicComponent.revalidate();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                logicComponent.setLocation(p);
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
