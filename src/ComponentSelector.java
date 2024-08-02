import javax.swing.*;
import java.awt.*;

public class ComponentSelector extends JPanel {

    ComponentSelector(){
        super();
        setLayout(new BorderLayout(20, 15));
        setBackground(Constants.BACKGROUND_COLOR);
        Frame frame = (JFrame) SwingUtilities.getWindowAncestor(this);


        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,5));
        buttonSubPanel.setBackground(Constants.BACKGROUND_COLOR);


        buttonSubPanel.add(new LogicComponent(false,Color.WHITE));
        add(buttonSubPanel);


    }
}
