import javax.swing.*;
import java.awt.*;

public class LeftButton extends JButton {

    LeftButton(String text){
        this.setText(text);
        this.setPreferredSize(new Dimension(50,75));
        this.setEnabled(false);
        this.setFont(new Font("MV Boli", Font.PLAIN, 15));
    }
}
