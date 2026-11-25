import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatsInput extends JDialog implements ActionListener {

    JButton button;
    JTextField textField;
    public String result = "";

    // Pass a parent Frame and make it modal (true)
    public StatsInput(Frame parent, String question){
        super(parent, "Input Required", true); // 'true' makes it modal!
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout());

        button = new JButton("Submit");
        button.addActionListener(this);

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 40));
        textField.setFont(new Font("Calibri", Font.PLAIN, 20));
        textField.setForeground(Color.GREEN);
        textField.setBackground(Color.BLACK);
        textField.setCaretColor(Color.CYAN);
        textField.setText(question);

        this.add(textField);
        this.add(button);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button){
            result = textField.getText();
            dispose(); // This closes the dialog and resumes your main code!
        }
    }
}