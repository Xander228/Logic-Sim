import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LogicSelectable extends JComponent {
    Color color;
    boolean draggable;
    int mouseX, mouseY;
    int startingX, startingY;

    MouseAdapter mouseAdapter;
    MouseMotionAdapter mouseMotionAdapter;

    LogicSelectable(Boolean draggable, Color color) {

        setPreferredSize(new Dimension(100,40));
        setMaximumSize(new Dimension(100,40));
        setBorder(new MatteBorder(2,2,2,2,Color.BLACK));
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

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                if (!isHovered(e.getLocationOnScreen())) return;
                if (!draggable) createChild();
                grabFocus();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() != MouseEvent.BUTTON1) return;
                if (!draggable) return;
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
                int newX = startingX + deltaX;
                int newY = startingY + deltaY;
                newX = Math.min(pane.getWidth() - getWidth() - Constants.BORDER_WIDTH,
                        Math.max(Constants.BORDER_WIDTH, newX));
                newY = Math.min(pane.getHeight() - getHeight() - Constants.BORDER_WIDTH,
                        Math.max(2 * Constants.BORDER_WIDTH + Constants.BUTTON_PANEL_HEIGHT, newY));
                setLocation(newX, newY);
                pane.repaint();
            }

        };

        pane.addMouseListener(mouseAdapter);
        pane.addMouseMotionListener(mouseMotionAdapter);
    }


    public void setStartingLocation(Point p){
        startingX = p.x;
        startingY = p.y;
        setLocation(startingX,startingY);
        repaint();
    }



    private void destruct(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
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
        LogicComponent logicComponent = new LogicComponent(color);
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
        System.out.println("Creating child");
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        Point p = getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(p, pane);

        LogicSelectable logicSelectable = new LogicSelectable(true,color);
        logicSelectable.setBounds(p.x, p.y, getWidth(), getHeight());
        logicSelectable.setStartingLocation(p);
        pane.add(logicSelectable);
        pane.setLayer(logicSelectable, JLayeredPane.DRAG_LAYER, 0);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
