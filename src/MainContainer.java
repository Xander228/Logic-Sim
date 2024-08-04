import javax.swing.*;
import java.awt.*;

public class MainContainer extends JLayeredPane {
    private JPanel mainPanel;
    private SimStage simStage;
    MainContainer(){

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createMatteBorder(
                Constants.BORDER_WIDTH,
                Constants.BORDER_WIDTH,
                Constants.BORDER_WIDTH,
                Constants.BORDER_WIDTH,
                Constants.ACCENT_COLOR));

        mainPanel.setBackground(Constants.ACCENT_COLOR); //Set the background color of the panel
        mainPanel.setLayout(new BorderLayout(Constants.BORDER_WIDTH,Constants.BORDER_WIDTH)); //Sets the edge offset of member panels to properly space them

        ButtonPanel buttonPanel = new ButtonPanel();
        simStage = new SimStage();
        ComponentSelector componentSelector = new ComponentSelector();

        mainPanel.add(buttonPanel,BorderLayout.NORTH);
        mainPanel.add(simStage);
        mainPanel.add(componentSelector,BorderLayout.SOUTH);

        setLayout(null);
        add(mainPanel);
        setLayer(mainPanel, JLayeredPane.DEFAULT_LAYER);
        revalidate();

    }

    @Override
    public void revalidate(){
        super.revalidate();
        this.setPreferredSize(mainPanel.getPreferredSize());
        mainPanel.setBounds(0,0,mainPanel.getPreferredSize().width,mainPanel.getPreferredSize().height);
        repaint();
    }

    public void addToBoard(Component component){
        simStage.add(component);
    }
}
