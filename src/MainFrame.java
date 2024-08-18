import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.Collections.sort;
import static javax.swing.UIManager.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super();

        UIManager.put("TabbedPane.background", Constants.PRIMARY_COLOR);
        UIManager.put("TabbedPane.darkShadow", Constants.BACKGROUND_COLOR);
        UIManager.put("TabbedPane.foreground", Constants.BACKGROUND_COLOR);
        UIManager.put("TabbedPane.light", Color.GRAY);
        UIManager.put("TabbedPane.selectHighlight", Color.WHITE);
        UIManager.put("TabbedPane.selected", Color.GRAY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        UIManager.put("Button.background", Constants.PRIMARY_COLOR);
        UIManager.put("Button.foreground", Constants.BACKGROUND_COLOR);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 16));
        UIManager.put("Button.border", BorderFactory.createMatteBorder(2, 2, 2, 2, Constants.ACCENT_COLOR));
        UIManager.put("Button.margin", new Insets(10, 20, 10, 20));


        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        setTitle("Logic Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MainContainer mainContainer = new MainContainer();

        setContentPane(mainContainer);
        pack();
        setFocusable(true);


        //Set the frame visible
        setVisible(true);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                mainContainer.validate();
            }
        });

    }


    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable(){
            public void run(){
                new MainFrame();
            }
        });
    }



}