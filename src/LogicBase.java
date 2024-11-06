import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public abstract class LogicBase extends JComponent {


    protected double boardX, boardY;
    protected double pixelX, pixelY;
    protected double boardInsetX, boardInsetY;



    LogicBase() {
        setOpaque(false);
        setLayout(null);

        /*
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

         */


    }

    private Point2D.Double pixelToBoard(Point2D.Double pixel){
        SimStage simStage = (SimStage) getParent();
        return new Point2D.Double(
                pixel.getX() / SimStage.cellWidth - simStage.viewPortOffsetX + boardInsetX,
                pixel.getY() / SimStage.cellWidth - simStage.viewPortOffsetY + boardInsetY);
    }

    private Point2D.Double boardToPixel(Point2D.Double board){
        SimStage simStage = (SimStage) getParent();
        return new Point2D.Double(
                (board.getX() + simStage.viewPortOffsetX - boardInsetX) * SimStage.cellWidth,
                (board.getY() + simStage.viewPortOffsetY - boardInsetY) * SimStage.cellWidth);
    }

    public void snapToBoard(){
        Point2D board = pixelToBoard(new Point2D.Double(pixelX, pixelY));
        board.setLocation(Math.round(board.getX()), Math.round(board.getY()));
        setBoardLocation(board);
    }

    public Point2D getBoardLocation(){
        return new Point2D.Double(boardX, boardY);

    }

    public Point2D.Double getPixelLocation(){
        return new Point2D.Double(pixelX, pixelY);
    }

    public void setBoardLocationFromScreen(Point p){
        SwingUtilities.convertPointFromScreen(p, this.getParent());
        setBoardLocation(pixelToBoard(new Point2D.Double(p.x, p.y)));
    }

    public void setBoardLocation(Point2D p){
        setBoardLocation(p.getX(), p.getY());
    }

    public void setPixelLocation(Point2D.Double p){
        setPixelLocation(p.x, p.y);
    }

    public void setBoardLocation(double x, double y) {
        boardX = Math.round(x);
        boardY = Math.round(y);

        updateRelativeLocation();
    }

    public void setPixelLocation(double x, double y){
        pixelX = x;
        pixelY = y;
        Point2D board = pixelToBoard(new Point2D.Double(pixelX, pixelY));
        boardX = board.getX();
        boardY = board.getY();

        updateLocation();
    }

    public void updateRelativeLocation(){
        setPixelLocation(boardToPixel(new Point2D.Double(boardX,boardY)));
        repaint();
    }

    protected abstract Dimension updateDimensions();

    public void setBoardInset(double xInset, double yInset){
        boardInsetX = xInset;
        boardInsetY = yInset;
    }

    protected void updateLocation(){
        double cellWidth = SimStage.cellWidth;

        Dimension bounds = updateDimensions();
        double doubleWidth = (bounds.width + boardInsetX * 2) * cellWidth;
        double doubleHeight = (bounds.height + boardInsetY * 2) * cellWidth;

        setBounds(
                (int) Math.floor(pixelX),
                (int) Math.floor(pixelY),
                (int) Math.floor(doubleWidth),
                (int) Math.floor(doubleHeight));
    }

    public void setTop(){
        SimStage simStage = (SimStage) getParent();
        simStage.setTop(this);
        grabFocus();
    }

    public void setBottom(){
        SimStage simStage = (SimStage) getParent();
        simStage.setBottom(this);
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
