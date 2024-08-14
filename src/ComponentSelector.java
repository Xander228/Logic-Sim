import javax.swing.*;
import java.awt.*;

public class ComponentSelector extends JPanel {

    ComponentSelector(){
        super();
        setLayout(new BorderLayout(20, 15));
        setBackground(Constants.BACKGROUND_COLOR);


        JPanel selectorSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,5));
        selectorSubPanel.setBackground(Constants.BACKGROUND_COLOR);

        selectorSubPanel.add(new LogicSelectable(false,Color.RED));
        selectorSubPanel.add(new LogicSelectable(false,Color.ORANGE));
        selectorSubPanel.add(new LogicSelectable(false,Color.YELLOW));
        selectorSubPanel.add(new LogicSelectable(false,Color.GREEN));
        selectorSubPanel.add(new LogicSelectable(false,Color.BLUE));
        selectorSubPanel.add(new LogicSelectable(false,Color.MAGENTA));
        add(selectorSubPanel);


    }
}
