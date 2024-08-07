import java.awt.*;
import java.awt.geom.Rectangle2D;

public class DisplayController {


    public static void drawBoard(Graphics2D g2){
        double cellBoarderWidth = Constants.DEFAULT_CELL_WIDTH * Constants.CELL_BORDER_RATIO;

        double totalViewPortOffsetY = 0;//GamePanel.viewPortOffsetY + GamePanel.liveViewPortOffsetY;
        double totalViewPortOffsetX = 0;//GamePanel.viewPortOffsetX + GamePanel.liveViewPortOffsetX;

        int yMin = (int)Math.floor(-totalViewPortOffsetY);
        int yMax = (int)Math.ceil((g2.getClipBounds().getHeight() / Constants.DEFAULT_CELL_WIDTH) - totalViewPortOffsetY);
        int xMin = (int)Math.floor(-totalViewPortOffsetX);
        int xMax = (int)Math.ceil((g2.getClipBounds().getWidth() / Constants.DEFAULT_CELL_WIDTH) - totalViewPortOffsetX);

        for(int y = yMin; y < yMax; y++) {
            for(int x = xMin; x < xMax; x++) {
                boolean cell = false;//GamePanel.boardManager.getCell(x, y);
                if(x == 0 && y == 0) g2.setColor(cell ? Constants.HOME_LIVE_COLOR : Constants.HOME_COLOR);
                else if(y == 0) g2.setColor(cell ? Constants.X_LIVE_COLOR : Constants.X_COLOR);
                else if(x == 0) g2.setColor(cell ? Constants.Y_LIVE_COLOR : Constants.Y_COLOR);
                else if(cell) g2.setColor(Constants.LIVE_COLOR);
                else continue;

                ///*
                Rectangle2D rect = new Rectangle2D.Double(
                        (cellBoarderWidth / 2) + (x + totalViewPortOffsetX) * Constants.DEFAULT_CELL_WIDTH,
                        (cellBoarderWidth / 2) + (y + totalViewPortOffsetY) * Constants.DEFAULT_CELL_WIDTH,
                        Constants.DEFAULT_CELL_WIDTH - cellBoarderWidth,
                        Constants.DEFAULT_CELL_WIDTH - cellBoarderWidth);
                g2.fill(rect);
            }
        }
        if(cellBoarderWidth <= .2) return;
        g2.setColor(Constants.ACCENT_COLOR);
        for(int y = yMin; y < yMax; y++) {
            Rectangle2D rect = new Rectangle2D.Double(
                    0,
                    (-cellBoarderWidth / 2) + (y + totalViewPortOffsetY) * Constants.DEFAULT_CELL_WIDTH,
                    g2.getClipBounds().getWidth(),
                    cellBoarderWidth);
            g2.fill(rect);
        }
        for(int x = xMin; x < xMax; x++) {
            Rectangle2D rect = new Rectangle2D.Double(
                    (-cellBoarderWidth / 2) + (x + totalViewPortOffsetX) * Constants.DEFAULT_CELL_WIDTH,
                    0,
                    cellBoarderWidth,
                    g2.getClipBounds().getHeight());
            g2.fill(rect);
        }
    }



}
