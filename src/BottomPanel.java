import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JTabbedPane {

    public BottomPanel() {
        UIManager.put("TabbedPane.background", Color.RED);
        UIManager.put("TabbedPane.borderHighlightColor", Color.RED);
        UIManager.put("TabbedPane.darkShadow", Color.RED);

        UIManager.put("TabbedPane.foreground", Constants.BACKGROUND_COLOR);
        UIManager.put("TabbedPane.selectForeground", Color.RED);
        UIManager.put("TabbedPane.light", Color.RED);
        UIManager.put("TabbedPane.selectHighlight", Color.RED);
        UIManager.put("TabbedPane.selected", Color.RED);
        UIManager.put("TabbedPane.selectShadow", Color.RED);
        UIManager.put("TabbedPane.tabAreaBackground", Color.RED);
        UIManager.put("TabbedPane.tabInsets", Color.RED);
        UIManager.put("TabbedPane.selectBackground", Color.RED);
        UIManager.put("TabbedPane.underlineColor", Color.BLUE);
        UIManager.put("TabbedPane.tabSeparatorColor", Color.BLUE);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        //UIManager.put("TabbedPane.tabInsets", new Insets(0,0,0,0));
        //UIManager.put("TabbedPane.tabAreaInsets", new Insets(0,0,0,0));
        //UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(0,0,0,0));
        //UIManager.put("TabbedPaneUI", "javax.swing.plaf.basic.BasicTabbedPaneUI");



        this.getUI();
        System.out.println(UIManager.get("TabbedPaneUI"));
        add("One",new ComponentSelector());
        add("Two",new ComponentSelector());
        add("Three",new ComponentSelector());

        setTabPlacement(JTabbedPane.TOP);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);

    }
}
