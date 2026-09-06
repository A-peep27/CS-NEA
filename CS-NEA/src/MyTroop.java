import javax.swing.*;

public class MyTroop extends JPanel {

    int x;
    int y;
    int hp;
    int str;
    String name;
    int hight = 5;
    int width = 5;
    boolean isDead = false;

    MyTroop (String name2, int x2, int y2, int hp2, int str2){

        x = x2;
        y = y2;
        hp = hp2;
        str = str2;
        name = name2;

        this.setBounds(x, y, hight, width);

    }

    public void setTroopLocation(int along, int up){
        if ((x<= 0 | y<=0) | (x>= 480 | y>=380)){
            System.out.println("out of bounds");
        }
        else {
            x= along;
            y = up;
        }
        this.setLocation(x, y);
    }
    public void moveTroop(int along, int down){
        x += along;
        y += down;
        if ((x<= 0 | y<=0) | (x>= 475 | y>=400)){
            x-=along;
            y-= down;
        }
    }
    public void takeDamage(int damage){
        hp -= damage;
        if (hp <= 0){
            System.out.println(name + "has died");
            isDead = true;
        }
    }
    public boolean isDead(){
        return isDead;
    }
    public int getAttack(){
        return str;
    }
    public int getAttack(int type){
        int attack = 0;
        if (type == 0){
            attack = str;
        }
        else if(type == 1){
            attack = Math.toIntExact(Math.round(1.5 * str));
        }
        else if(type == 2){
            attack = Math.toIntExact((Math.round(0.5 * str)));
        }
        return attack;
    }
    public String getName(){
        return name;
    }
}
