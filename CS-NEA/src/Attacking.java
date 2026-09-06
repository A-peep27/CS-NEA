import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Attacking extends JFrame implements ActionListener {

    int hpYours;
    int hpTheirs;
    int maxHPYours;
    int maxHPTheirs;

    int strYours;
    int strTheirs;

    String nameYours;
    String nameTheirs;

    JPanel deadPanel;
    boolean isDead = false;

    JLabel hpLabelYours;
    JLabel hpLabelTheirs;

    JButton parryAttack;
    JButton normalAttack;
    JButton hevyAttack;

    JProgressBar hpBarYours;
    JProgressBar hpBarTheirs;

    JPanel[] yourAttackPanels;
    JPanel[] theirAttackPanels;


    Attacking(int hpY, int hpT, int strY, int strT, String nameY, String nameT ){

        hpYours = hpY;
        hpTheirs = hpT;
        strYours = strY;
        strTheirs = strT;
        nameYours = nameY;
        nameTheirs = nameT;
        maxHPYours = hpY;
        maxHPTheirs= hpT;

        this.setSize(500,540);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);

        hpLabelYours = new JLabel();
        hpLabelYours.setText(nameYours + " - "+hpYours+"/"+maxHPYours);
        hpLabelYours.setBounds(0,0,100,30);
        hpBarYours = new JProgressBar(0, maxHPYours);
        hpBarYours.setValue(hpYours);
        hpBarYours.setBounds(0,20,100,10);
        this.add(hpBarYours);
        this.add(hpLabelYours);

        hpLabelTheirs = new JLabel();
        hpLabelTheirs.setText(nameTheirs + " - "+hpTheirs+"/"+maxHPTheirs);
        hpLabelTheirs.setBounds(390,0,100,30);
        hpBarTheirs = new JProgressBar(0, maxHPTheirs);
        hpBarTheirs.setValue(hpTheirs);
        hpBarTheirs.setBounds(390,20,100,10);
        this.add(hpBarTheirs);
        this.add(hpLabelTheirs);


        parryAttack = new JButton();
        parryAttack.setText("parry");
        parryAttack.setBounds(0,400,166,100);
        parryAttack.addActionListener(this);
        this.add(parryAttack);

        normalAttack = new JButton();
        normalAttack.setText("normal");
        normalAttack.setBounds(166, 400, 167, 100);
        normalAttack.addActionListener(this);
        this.add(normalAttack);

        hevyAttack = new JButton();
        hevyAttack.setText("heavy");
        hevyAttack.setBounds(333, 400, 166,100);
        hevyAttack.addActionListener(this);
        this.add(hevyAttack);

        this.setVisible(true);
    }
    public void attack(){
        int attack = 0;
        int type = (int) Math.floor(Math.random() * 3);
        if (type == 0){
            attack = strTheirs;
        }
        else if(type == 1){
            attack = (int)(Math.floor(1.5 * strTheirs));
        }
        else{
            attack = (int)(Math.floor(0.5 * strTheirs));
        }
        hpYours -= attack;

        checkDead();
        this.setVisible(true);
    }
    public void checkDead(){
        if(hpYours <= 0){
            JLabel label = new JLabel();
            label.setText("YOU DIED");
            label.setForeground(Color.RED);

            isDead = true;
            deadPanel = new JPanel();

            deadPanel.setBackground(Color.BLACK);

            deadPanel.add(label);
            deadPanel.setBounds(200,225,100,50);

            deadPanel.setVisible(true);
            this.add(deadPanel);
        }
        if(hpTheirs <= 0){
            JLabel label = new JLabel();
            label.setText("YOU WIN");
            label.setForeground(Color.GREEN);

            isDead = true;
            deadPanel = new JPanel();

            deadPanel.setBackground(Color.BLACK);

            deadPanel.add(label);

            deadPanel.setBounds(200,225,100,50);

            deadPanel.setVisible(true);
            this.add(deadPanel);
            System.out.println("you win");
        }

        this.revalidate();
        this.repaint();
    }

    public void resetHpStats(){
        hpBarTheirs.setValue(hpTheirs);
        hpBarYours.setValue(hpYours);
        hpLabelYours.setText(nameYours + " - "+hpYours+"/"+maxHPYours);
        hpLabelTheirs.setText(nameTheirs + " - "+hpTheirs+"/"+maxHPTheirs);
    }

    public void makeAttackGrid(){
        yourAttackPanels = new JPanel[6];
        MyPanel panel = new MyPanel();
        panel.setLocation(100, 250);

        for (int i = 0; i < 6; i++) {
            panel.setLocation((100 + 50 * i), 200);
            yourAttackPanels[i] = (panel);
        }

        theirAttackPanels = new JPanel[6];
        panel.setLocation(100, 200);

        for (int i = 0; i < 6; i++) {
            panel.setLocation((100 + 50 * i), 200);
            theirAttackPanels[i] = (panel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()== parryAttack){
           hpTheirs -= (int)(Math.floor(0.5 * strYours));
            System.out.println("light");
        }
        else if(e.getSource() ==normalAttack){
            hpTheirs -= (int)(1 * strYours);
            System.out.println("medium");
        }
        else if(e.getSource()== hevyAttack){
            hpTheirs-= (int)(Math.floor(1.5 * strYours));
            System.out.println("heavy");
        }

        resetHpStats();
        checkDead();
    }
}
