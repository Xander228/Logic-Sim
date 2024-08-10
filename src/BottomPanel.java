import javax.swing.*;

public class BottomPanel extends JTabbedPane {

    public BottomPanel() {
        add(new ComponentSelector());
        add(new ComponentSelector());
        add(new ComponentSelector());
        setBackground(Constants.PRIMARY_COLOR);
        setForegroundAt(1, Constants.PRIMARY_COLOR);

    }
}
