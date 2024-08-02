import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public static MainFrame frame;


    public MainFrame() {
        super();

        UIManager.put("ToolTip.foreground", Constants.BACKGROUND_COLOR);
        UIManager.put("ToolTip.background", Constants.PRIMARY_COLOR);
        UIManager.put("ToolTip.border",BorderFactory.createMatteBorder(1,1,1,1,Constants.BACKGROUND_COLOR));


        setTitle("Logic Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane jLayeredPane = new JLayeredPane();
        MainPanel mainPanel = new MainPanel();


        jLayeredPane.setLayout(new OverlayLayout(jLayeredPane));

        jLayeredPane.add(mainPanel);
        jLayeredPane.setLayer(mainPanel, JLayeredPane.DEFAULT_LAYER);
        setContentPane(jLayeredPane);
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