import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class LogicDisplayController {

    public static Graphics2D getGraphics2D(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        return g2d;
    }

    public static String[] calculateDrawableName(Component component, LogicAttributes attributes) {
        return attributes.name.split(" ");
    }

    public static double maxInputWidth(Component component, LogicAttributes attributes){
        FontMetrics fontMetrics = component.getFontMetrics(Constants.CONNCETOR_FONT.deriveFont(13.75f));

        double maxInputWidth = 0;
        for (ConnectorAttributes attribute : attributes.inputAttributes) {
            if (attribute == null) continue;
            maxInputWidth = Math.max(maxInputWidth,
                    fontMetrics.stringWidth(attribute.name) / 10.0);
        }
        return maxInputWidth;
    }

    public static double maxOutputWidth(Component component, LogicAttributes attributes){
        FontMetrics fontMetrics = component.getFontMetrics(Constants.CONNCETOR_FONT.deriveFont(13.75f));

        double maxOutputWidth = 0;
        for (ConnectorAttributes attribute : attributes.outputAttributes) {
            if (attribute == null) continue;
            maxOutputWidth = Math.max(maxOutputWidth,
                    fontMetrics.stringWidth(attribute.name) / 10.0);
        }
        return maxOutputWidth;
    }

    public static double calculateWidth(Component component, LogicAttributes attributes){
        FontMetrics fontMetrics = component.getFontMetrics(Constants.COMPONENT_FONT.deriveFont(13.75f));

        double maxNameWidth = 0;
        String[] drawableName = calculateDrawableName(component, attributes);
        for(String string : drawableName){
            maxNameWidth = Math.max(maxNameWidth, fontMetrics.stringWidth(string) / 10.0);
        }
        double nameWidth = maxNameWidth;

        return maxInputWidth(component, attributes) + maxOutputWidth(component, attributes) + nameWidth + 3;
    }


    public static void paint(Graphics g,
                             Component component,
                             ArrayList<Connector> inputConnectors,
                             ArrayList<Connector> outputConnectors,
                             LogicAttributes attributes){

        Graphics2D g2d = getGraphics2D(g);
        g2d.setColor(attributes.color);

        double cellWidth = SimStage.cellWidth;

        int minHeightInCells = Math.max(inputConnectors.size(), outputConnectors.size()) * 2;
        double doubleHeight = minHeightInCells * cellWidth;
        int minWidthInCells = (int) Math.ceil(LogicDisplayController.calculateWidth(component,attributes)) + 2;
        double doubleWidth = minWidthInCells * cellWidth;

        Shape body = new Rectangle2D.Double(
                1 * cellWidth,
                .1 * cellWidth,
                doubleWidth - 2 * cellWidth,
                doubleHeight - .2 * cellWidth);

        g2d.fill(body);

        for(Connector connector : inputConnectors) {
            if(connector == null) continue;
            connector.paintComponent(g2d);
        }
        for(Connector connector : outputConnectors) {
            if(connector == null) continue;
            connector.paintComponent(g2d);
        }


        g2d.setFont(Constants.COMPONENT_FONT.deriveFont((float)(cellWidth * 1.375)));

        String[] strings = LogicDisplayController.calculateDrawableName(component, attributes);
        double offset = (doubleHeight - (g2d.getFontMetrics().getHeight() * strings.length)) / 2;
        for (int i = 0; i < strings.length; i++) {
            double sideOffset = (doubleWidth - g2d.getFontMetrics().stringWidth(strings[i])) / 2.0;
            g2d.drawString(strings[i],
                    (float) (sideOffset),
                    (float) (offset + 1.375 * (i + 1) * cellWidth));

        }
    }
}
