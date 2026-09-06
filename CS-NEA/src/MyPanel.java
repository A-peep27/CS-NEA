import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel{

    int x = 0;
    int y = 0;

    int boundsX = 480;
    int boundsY = 380;

    MyPanel(){
        this.setBounds(x,y, 50, 50);
        this.setLayout(null);
        this.setBackground(new Color(219, 219, 219));
    }

    MyPanel(int x2, int y2, int width, int hight, Color backroundColor){
        x = x2;
        y = y2;

        this.setBounds(x,y, width, hight);
        this.setLayout(null);
        this.setBackground(backroundColor);
    }

    public void movePane(int along, int down){
        x += along;
        y += down;
        if (x<= 0 | y<=0){
            x-=along;
            y-= down;
        }
        else if (x>= boundsX | y>=boundsY){
            x-= along;
            y-= down;
        }
        this.setLocation((x), (y));
    }
    public void setBounds(int boundx, int boundy){
        boundsX = boundx;
        boundsY = boundy;
    }
}
