import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class JButtonTextChangeTest extends JFrame {
    private JTextField textField;
    private JButton button;
    public JButtonTextChangeTest() {
        setTitle("JButtonTextChange Test");
        setLayout(new FlowLayout());
        textField = new JTextField(20);
        button = new JButton("Initial Button");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!textField.getText().equals(""))
                    button.setText(textField.getText());
            }
        });
        add(textField);
        add(button);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new JButtonTextChangeTest();
    }
}