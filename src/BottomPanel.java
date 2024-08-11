import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JTabbedPane {

    public BottomPanel() {
        UIManager.put("TabbedPane.tabAreaBackground", Color.RED);
        UIManager.put("TabbedPane.unselectedBackground", Color.ORANGE);
        UIManager.put("TabbedPane.selectHighlight", Color.YELLOW);
        UIManager.put("TabbedPane.highlight", Color.GREEN);
        UIManager.put("TabbedPane.borderHightlightColor", Color.CYAN);
        UIManager.put("TabbedPane.contentAreaColor", Constants.PRIMARY_COLOR);
        UIManager.put("TabbedPane.focus", Color.MAGENTA);
        UIManager.put("TabbedPane.selected", Color.RED);
        UIManager.put("TabbedPane.light", Color.RED);
        UIManager.put("TabbedPane.background", Constants.PRIMARY_COLOR);
        UIManager.put("TabbedPane.foreground", Constants.PRIMARY_COLOR);

        this.getUI();
        //for(Object key : UIManager.getDefaults().keySet()) System.out.println(key + " - " + UIManager.get(key));

        add(new ComponentSelector());
        add(new ComponentSelector());
        add(new ComponentSelector());
        setBackground(Constants.PRIMARY_COLOR);
        setForeground(Constants.ACCENT_COLOR);

    }
}
