import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    MainPanel(){
        this.setBorder(BorderFactory.createMatteBorder(10,10,10,10,Constants.ACCENT_COLOR)); //Add a border around the frame
        this.setBackground(Constants.ACCENT_COLOR); //Set the background color of the panel
        this.setLayout(new BorderLayout(10,10)); //Sets the edge offset of member panels to properly space them

        ButtonPanel buttonPanel = new ButtonPanel();
        SimStage simStage = new SimStage();
        //ComponentSelector componentSelector = new ComponentSelector();

        this.add(buttonPanel,BorderLayout.NORTH);
        this.add(simStage);
        //mainPanel.add(componentSelector,BorderLayout.SOUTH);
    }
}
