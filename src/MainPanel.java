import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    public static MainPanel mainPanel;
    MainPanel(){
        this.setBorder(BorderFactory.createMatteBorder(10,10,10,10,Constants.ACCENT_COLOR)); //Add a border around the frame
        this.setBackground(Constants.ACCENT_COLOR); //Set the background color of the panel
        this.setLayout(new BorderLayout(10,10)); //Sets the edge offset of member panels to properly space them

        ButtonPanel buttonPanel = new ButtonPanel();
        SimStage simStage = new SimStage();
        ComponentSelector componentSelector = new ComponentSelector();

        add(buttonPanel,BorderLayout.NORTH);
        add(simStage);
        add(componentSelector,BorderLayout.SOUTH);
    }
}
