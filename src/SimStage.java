import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class SimStage extends JPanel {

    private ArrayList<Component> components;
    SimStage() {
        super();
        components = new ArrayList<Component>();
        setPreferredSize(new Dimension(Constants.DESIRED_VIEWPORT_WIDTH, Constants.DESIRED_VIEWPORT_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
            }
        });

    }

    public void setTop(Component component) {
        components.remove(component);
        components.addLast(component);
        for(Component c : components){
            add(c, components.size() - components.indexOf(component) - 1);
        }
        repaint();
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }
}
