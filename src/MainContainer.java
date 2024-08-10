import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        ButtonPanel topPanel = new ButtonPanel();
        simStage = new SimStage();
        BottomPanel bottomPanel = new BottomPanel();

        mainPanel.add(topPanel,BorderLayout.NORTH);
        mainPanel.add(simStage);
        mainPanel.add(bottomPanel,BorderLayout.SOUTH);

        setLayout(null);
        add(mainPanel);
        setLayer(mainPanel, JLayeredPane.DEFAULT_LAYER);

        this.setPreferredSize(mainPanel.getPreferredSize());
        mainPanel.setBounds(0,0,mainPanel.getPreferredSize().width,mainPanel.getPreferredSize().height);
        repaint();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getParent().addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        validate();
                    }
                });
            }
        });


    }

    @Override
    public void validate(){
        super.validate();
        if(this.getParent().getSize().equals(new Dimension(0,0))) this.getParent().setSize(mainPanel.getPreferredSize());
        this.setPreferredSize(this.getParent().getSize());
        mainPanel.setBounds(0,0,this.getParent().getSize().width,this.getParent().getSize().height);
        repaint();
    }

    public void addToBoard(Component component){
        simStage.add(component);
    }
}
