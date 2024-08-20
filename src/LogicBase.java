import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public abstract class LogicBase extends JComponent {


    protected int boardX, boardY;
    protected int pixelX, pixelY;



    LogicBase() {
        setOpaque(false);
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

    public Point getPixelLocation(){
        return new Point(pixelX, pixelY);
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
    }

    public void updateRelativeLocation(){
        setPixelLocation(boardToPixel(new Point(boardX,boardY)));
    }

    protected abstract Dimension updateDimensions();

    protected void updateLocation(){
        double cellWidth = SimStage.cellWidth;

        Dimension bounds = updateDimensions();
        double doubleWidth = bounds.width * cellWidth;
        double doubleHeight = bounds.height * cellWidth;


        setBounds(
                (int) (pixelX),
                (int) (pixelY),
                (int) doubleWidth,
                (int) doubleHeight);
    }

    public void setTop(){
        SimStage simStage = (SimStage) getParent();
        simStage.setTop(this);
        grabFocus();
    }

    protected void destruct(){

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



}
