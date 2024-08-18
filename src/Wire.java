import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Wire extends JComponent {
    private Connector start;
    private Connector end;
    private boolean isOn;
    private Color color;
    private ArrayList<Point> controlPoints;

    public Wire(Color color){
        this.color = color;
        this.isOn = false;
        this.controlPoints = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));
        if(isOn){
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
        }
        if(start != null && end != null){
            for(int i = 0; i < controlPoints.size() - 1; i++){
                g2.drawLine(controlPoints.get(i).x, controlPoints.get(i).y, controlPoints.get(i + 1).x, controlPoints.get(i + 1).y);
            }
        }
    }
}
