import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public static MainFrame frame;
    public JPanel mainPanel;

    public MainFrame() {
        super();

        UIManager.put("ToolTip.foreground", Constants.BACKGROUND_COLOR);
        UIManager.put("ToolTip.background", Constants.PRIMARY_COLOR);
        UIManager.put("ToolTip.border",BorderFactory.createMatteBorder(1,1,1,1,Constants.BACKGROUND_COLOR));


        setTitle("Logic Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createMatteBorder(10,10,10,10,Constants.ACCENT_COLOR)); //Add a border around the frame
        mainPanel.setBackground(Constants.ACCENT_COLOR); //Set the background color of the panel
        mainPanel.setLayout(new BorderLayout(10,10)); //Sets the edge offset of member panels to properly space them

        ButtonPanel buttonPanel = new ButtonPanel();
        SimStage simStage = new SimStage();
        ComponentSelector componentSelector = new ComponentSelector();

        mainPanel.add(buttonPanel,BorderLayout.NORTH);
        mainPanel.add(simStage);
        mainPanel.add(componentSelector,BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setFocusable(true);

        //Set the frame visible
        setVisible(true);
    }

    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable(){
            public void run(){
                frame = new MainFrame();
            }
        });
    }



}