import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyPanel extends JPanel{

    int x = 0;
    int y = 0;

    int boundsX = 500;
    int boundsY = 380;

    Color defualtColor;
    Color currentCOlor;

    MyPanel(){
        this.setBounds(x,y, 50, 50);
        this.setLayout(null);
        this.setBackground(new Color(255, 255, 255, 255));
    }

    MyPanel(int x2, int y2, int width, int hight, Color backroundColor){
        x = x2;
        y = y2;

        this.setBounds(x,y, width, hight);
        this.setLayout(null);
        defualtColor = backroundColor;
        currentCOlor = backroundColor;
        this.setBackground(defualtColor);
        this.setOpaque(true);
        //this.addMouseListener(this);
        this.setForeground(Color.BLACK);
        //this.setFont(new Font("calibri",Font.PLAIN, 10));
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
    public void resetBounds(int boundx, int boundy){
        boundsX = boundx;
        boundsY = boundy;
    }

    public int[] getXY(){
        return new int[]{x, y};
    }

    public void changeColor(Color color){
        currentCOlor = color;
        this.setBackground(color);
    }
    public void resetColor(){
        this.setBackground(defualtColor);
        currentCOlor = defualtColor;
    }

    public void setDefualtColor(Color color){
        defualtColor = color;
    }

    public Color getColor(){
        return currentCOlor;
    }
    /*

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        changeColor(Color.BLUE);
        defualtColor = Color.BLUE;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        changeColor(Color.RED);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        changeColor(defualtColor);

    }

     */
}
