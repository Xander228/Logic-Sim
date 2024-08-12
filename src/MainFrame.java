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

    public static MainFrame frame;


    public MainFrame() {
        super();
        /*
        javax.swing.plaf.metal.MetalLookAndFeel
        Nimbus javax.swing.plaf.nimbus.NimbusLookAndFeel
        CDE/Motif com.sun.java.swing.plaf.motif.MotifLookAndFeel
        Windows com.sun.java.swing.plaf.windows.WindowsLookAndFeel
        Windows Classic com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
         */
        //for(UIManager.LookAndFeelInfo info :UIManager.getInstalledLookAndFeels()) System.out.println(info);
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
                frame = new MainFrame();
            }
        });
    }



}