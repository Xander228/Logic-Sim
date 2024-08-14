import javax.swing.*;
import java.awt.*;

public class ComponentSelector extends JPanel {

    ComponentSelector(){
        super();
        setLayout(new BorderLayout(20, 15));
        setBackground(Constants.BACKGROUND_COLOR);


        JPanel selectorSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,5));
        selectorSubPanel.setBackground(Constants.BACKGROUND_COLOR);

        for(int i = 0; i < 7; i++)
            selectorSubPanel.add(new LogicSelectable(false,randomColor()));
        add(selectorSubPanel);


    }

    private Color randomColor(){
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }

    public boolean checkIfSelected(){
        BottomPanel bottomPanel = (BottomPanel) SwingUtilities.getAncestorOfClass(BottomPanel.class, this);
        return bottomPanel.checkIfSelected(this);
    }
}
