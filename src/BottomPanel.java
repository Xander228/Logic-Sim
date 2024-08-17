import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BottomPanel extends JTabbedPane {

    public BottomPanel() {
        UIManager.put("TabbedPane.background", Constants.PRIMARY_COLOR);
        UIManager.put("TabbedPane.darkShadow", Constants.BACKGROUND_COLOR);
        UIManager.put("TabbedPane.foreground", Constants.BACKGROUND_COLOR);
        UIManager.put("TabbedPane.light", Color.GRAY);
        UIManager.put("TabbedPane.selectHighlight", Color.WHITE);
        UIManager.put("TabbedPane.selected", Color.GRAY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        this.getUI();
        add("Basic", new ComponentSelector());
        add("Custom", new ComponentSelector());
        add("Settings", new ComponentSelector());

        setTabPlacement(JTabbedPane.TOP);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);


        EventQueue.invokeLater(new Runnable() {
            public void run() {
                addListeners();
            }
        });
    }

    public boolean checkIfSelected(Component component){
        int index = indexOfComponent(component);
        if(getSelectedIndex() == index){
            return true;
        }
        return false;
    }

    private void addListeners(){
        JLayeredPane pane = (JLayeredPane) SwingUtilities.getAncestorOfClass(JLayeredPane.class, this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pane.dispatchEvent(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pane.dispatchEvent(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                pane.dispatchEvent(e);
            }
        });
    }

}
