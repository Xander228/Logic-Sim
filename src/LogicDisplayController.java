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
        g2d.setFont(new Font("Arial", Font.BOLD, 12).deriveFont(13.75f));
        return g2d;
    }

    public static String[] calculateDrawableName(Component component, LogicAttributes attributes) {
        if(!attributes.verticalName) return attributes.name.split(" ");
        Graphics2D g2d = getGraphics2D(component.getGraphics());
        int minHeightInCells = Math.max(attributes.inputAttributes.size(), attributes.outputAttributes.size()) * 2;
        if(minHeightInCells == 0) minHeightInCells = 10;

        ArrayList<String> strings = new ArrayList<String>();
        String name = attributes.name;
        while(g2d.getFontMetrics().stringWidth(name) / 10.0 > minHeightInCells) {
            for (int i = name.length(); i > 0; i--) {
                if (g2d.getFontMetrics().stringWidth(name.substring(0, i)) / 10.0 > minHeightInCells)
                    continue;
                boolean hasSpace = false;
                for (int j = i - 1; j > 0; j--) {
                    if (name.charAt(j) == ' ') {
                        hasSpace = true;
                        strings.add(name.substring(0, j));
                        name = name.substring(j + 1);
                        break;
                    }
                }
                if(!hasSpace) {
                    strings.add(name.substring(0, i));
                    name = name.substring(i);
                    break;
                }
                break;
            }
        }
        if(!name.isEmpty()) strings.add(name);
        return strings.toArray(String[]::new);
    }

    public static double maxInputWidth(Component component, LogicAttributes attributes){
        Graphics2D g2d = getGraphics2D(component.getGraphics());

        double maxInputWidth = 0;
        for (ConnectorAttributes attribute : attributes.inputAttributes) {
            if (attribute == null) continue;
            maxInputWidth = Math.max(maxInputWidth,
                    g2d.getFontMetrics().stringWidth(attribute.name) / 10.0);
        }
        return maxInputWidth;
    }

    public static double maxOutputWidth(Component component, LogicAttributes attributes){
        Graphics2D g2d = getGraphics2D(component.getGraphics());

        double maxOutputWidth = 0;
        for (ConnectorAttributes attribute : attributes.outputAttributes) {
            if (attribute == null) continue;
            maxOutputWidth = Math.max(maxOutputWidth,
                    g2d.getFontMetrics().stringWidth(attribute.name) / 10.0);
        }
        return maxOutputWidth;
    }

    public static double calculateWidth(Component component, LogicAttributes attributes){
        Graphics2D g2d = getGraphics2D(component.getGraphics());

        double maxNameWidth = 0;
        String[] drawableName = calculateDrawableName(component, attributes);
        for(String string : drawableName){
            maxNameWidth = Math.max(maxNameWidth, g2d.getFontMetrics().stringWidth(string) / 10.0);
        }
        double nameWidth = attributes.verticalName ? drawableName.length : maxNameWidth;

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


        g2d.setFont(new Font("Arial", Font.BOLD, 12).deriveFont((float)(cellWidth * 1.375)));

        if(attributes.verticalName) {
            AffineTransform originalContext = g2d.getTransform();
            AffineTransform at = g2d.getTransform();
            at.quadrantRotate(1);
            g2d.setTransform(at);

            String[] strings = LogicDisplayController.calculateDrawableName(component, attributes);
            double offset = (LogicDisplayController.calculateWidth(component, attributes) % 1.0 + .5) / 2.0;
            offset += LogicDisplayController.maxInputWidth(component, attributes) + 2;
            for (int i = 0; i < strings.length; i++) {
                double topOffset = (doubleHeight - g2d.getFontMetrics().stringWidth(strings[i])) / 2.0;
                g2d.drawString(strings[i],
                        (float) (topOffset),
                        (float) (-(offset + 1.375 * (strings.length - i - 1)) * cellWidth));

            }
            g2d.setTransform(originalContext);
        }
        else {
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
}
