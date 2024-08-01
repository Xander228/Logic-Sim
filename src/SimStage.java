import javax.swing.*;
import java.awt.*;


public class SimStage extends JPanel {

    SimStage() {
        super();

        setPreferredSize(new Dimension(Constants.DESIRED_VIEWPORT_WIDTH, Constants.DESIRED_VIEWPORT_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        /*

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MainFrame.frame.mainPanel.grabFocus();
                double totalViewPortOffsetY = GamePanel.viewPortOffsetY + GamePanel.liveViewPortOffsetY;
                double totalViewPortOffsetX = GamePanel.viewPortOffsetX + GamePanel.liveViewPortOffsetX;
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if (patternPlacer != null) {
                        patternPlacer.writeToBoard();
                        patternPlacer = null;
                    }
                    else if(patternPicker != null) {
                        patternPicker.mousePressed();
                    }
                    else invertCell(
                            (int)Math.floor((e.getX() / cellWidth - totalViewPortOffsetX)) ,
                            (int)Math.floor((e.getY() / cellWidth - totalViewPortOffsetY))
                    );
                }
                if(e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
                    isDragging = true;
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(patternPicker != null) {
                        String myString = patternPicker.mouseReleased();
                        patternPicker = null;

                        StringSelection stringSelection = new StringSelection(myString);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
                    isDragging = false;
                    viewPortOffsetX += liveViewPortOffsetX;
                    viewPortOffsetY += liveViewPortOffsetY;
                    liveViewPortOffsetX = 0;
                    liveViewPortOffsetY = 0;
                }
            }
        });
        */

    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }
}
