import javax.swing.*;
import java.awt.*;

public class UpButton extends JButton{

    UpButton(String text){
        this.setText(text);
        this.setPreferredSize(new Dimension(50,35));
        this.setEnabled(false);
        this.setFont(new Font("MV Boli", Font.PLAIN, 10));
    }
}
