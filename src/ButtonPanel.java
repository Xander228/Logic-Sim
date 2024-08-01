import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonPanel extends JPanel {

    ButtonPanel(MainFrame frame){
        super();
        setLayout(new BorderLayout(20, 15));
        setBackground(Constants.BACKGROUND_COLOR);

        class GameButton extends JButton {
            GameButton(String text){
                super(text);
                this.setFocusable(false);
                this.setPreferredSize(new Dimension(80, 40));
                this.setBackground(Constants.PRIMARY_COLOR);
                this.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Constants.ACCENT_COLOR));
                this.setFont(new Font("Arial", Font.BOLD, 16));
                this.setForeground(Constants.BACKGROUND_COLOR);
            }
        }

        JButton start = new GameButton("Start");
        start.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GamePanel.boardManager.startTimer();
            }
        });

        JButton step = new GameButton("Step");
        step.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GamePanel.boardManager.stopTimer();
                GamePanel.boardManager.nextGeneration();
            }
        });

        JButton stop = new GameButton("Stop");
        stop.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GamePanel.boardManager.stopTimer();
            }
        });

        JButton reset = new GameButton("Reset");
        reset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GamePanel.boardManager.stopTimer();
                GamePanel.resetBoard();
            }
        });

        JButton random = new GameButton("Random");
        random.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                GamePanel.boardManager.stopTimer();
                GamePanel.randomizeBoard();
            }
        });

        JButton importString = new GameButton("Import / Export");
        importString.setPreferredSize(new Dimension(140, 40));
        importString.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(GamePanel.patternImporter != null) GamePanel.patternImporter.dispose();
                GamePanel.patternImporter = new PatternImporter();
            }
        });

        JSpinner speed = new JSpinner(new SpinnerNumberModel((int)(1000.0 / Constants.DEFAULT_GAME_DELAY),
                0,
                1000,
                5));

        JFormattedTextField spinnerTextField = ((JSpinner.NumberEditor) speed.getEditor()).getTextField();
        ((NumberFormatter) spinnerTextField.getFormatter()).setAllowsInvalid(false);
        spinnerTextField.setHorizontalAlignment(JTextField.CENTER);

        speed.setPreferredSize(new Dimension(80, 40));
        speed.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Constants.ACCENT_COLOR));
        speed.setFont(new Font("Arial", Font.BOLD, 16));
        speed.setToolTipText("Simulation Speed");
        speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                GamePanel.updateTimerSpeed((int)speed.getValue());
            }
        });

        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                frame.mainPanel.grabFocus();
            }
        });

        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20,5));
        buttonSubPanel.setBackground(Constants.BACKGROUND_COLOR);


        buttonSubPanel.add(stop);
        buttonSubPanel.add(step);
        buttonSubPanel.add(start);
        buttonSubPanel.add(importString);
        buttonSubPanel.add(reset);
        buttonSubPanel.add(random);
        buttonSubPanel.add(speed);

        add(buttonSubPanel);


    }
}
