import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;

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
    boolean alreadyAttacked = false;

    JLabel hpLabelYours;
    JLabel hpLabelTheirs;

    JButton parryAttack;
    JButton normalAttack;
    JButton hevyAttack;
    JButton showAttacks;

    JProgressBar hpBarYours;
    JProgressBar hpBarTheirs;

    JPanel[] yourAttackPanels = new JPanel[6];
    JPanel[] theirAttackPanels = new JPanel[6];
    int yourAttackEnd = 0;
    int[] yourAttackOrder;
    int[] theirAttackOrder;
    final String DB_URL = "jdbc:h2:./my_database";
    final String DB_USER = "";
    final String DB_PASSWORD = "";


    Random random = new Random();


    Attacking(int hpY, int hpT, int strY, int strT, String nameY, String nameT ){

        hpYours = hpY;
        hpTheirs = hpT;
        strYours = strY;
        strTheirs = strT;
        nameYours = nameY;
        nameTheirs = nameT;
        maxHPYours = hpY;
        maxHPTheirs= hpT;
        intializeScreen();

    }
    Attacking(int hpY, int hpYM, int hpT, int hpYT, int strY, int strT, String nameY, String nameT ){
        hpYours = hpY;
        hpTheirs = hpT;
        strYours = strY;
        strTheirs = strT;
        nameYours = nameY;
        nameTheirs = nameT;
        maxHPYours = hpYM;
        maxHPTheirs= hpYT;
        intializeScreen();
    }

    public void intializeScreen(){
        this.setSize(500,540);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        parryAttack.setText("rock");
        parryAttack.setBounds(0,400,166,100);
        parryAttack.addActionListener(this);
        this.add(parryAttack);

        normalAttack = new JButton();
        normalAttack.setText("paper");
        normalAttack.setBounds(166, 400, 167, 100);
        normalAttack.addActionListener(this);
        this.add(normalAttack);

        hevyAttack = new JButton();
        hevyAttack.setText("scisors");
        hevyAttack.setBounds(333, 400, 166,100);
        hevyAttack.addActionListener(this);
        this.add(hevyAttack);


        showAttacks = new JButton("revial");
        showAttacks.setBounds(166, 0, 166,50);
        showAttacks.addActionListener(this);
        this.add(showAttacks);

        yourAttackOrder = new int[6];
        theirAttackOrder = new int[6];

        this.setVisible(true);
        showAttacks.setEnabled(false);
    }

    public void attack(){
        int attack = 0;
        int type = (int) (Math.floor(Math.random() * 3));
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

            if(hpYours<=0){
                hpYours = 0;
            }

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
            if (hpTheirs<=0){
                hpTheirs = 0;
            }
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
        JPanel panel = new JPanel();
        setAttacks();
        panel.setLocation(100, 250);
        checkDead();

        if (alreadyAttacked){
            for (int i = 0; i<6; i++) {
                this.remove(theirAttackPanels[i]);
                this.remove(yourAttackPanels[i]);
            }
        }
        for (int i = 0; i < 6; i++) {
            JLabel label = new JLabel();
            yourAttackPanels[i] = (new JPanel());

            if (yourAttackOrder[i] == 1){
                label.setText("scissors");
            }
            else if(yourAttackOrder[i] == 2){
                label.setText("paper");
            }
            else{
                label.setText("rock");
            }

            yourAttackPanels[i].setSize(50,50);
            yourAttackPanels[i].add(label);
            yourAttackPanels[i].setLocation((100+ 50 * i), 250);

            this.add(yourAttackPanels[i]);
        }


        panel.setLocation(100, 200);


        for (int i = 0; i < 6; i++) {
            System.out.println(theirAttackOrder[i]);
            theirAttackPanels[i] = (new JPanel());
            JLabel label = new JLabel();

            if (theirAttackOrder[i] == 1){
                label.setText("sissors");
            }
            else if(theirAttackOrder[i] == 3){
                label.setText("rock");
            }
            else{
                label.setText("paper");
            }

            theirAttackPanels[i].setSize(50,50);
            theirAttackPanels[i].add(label);
            theirAttackPanels[i].setLocation((100+ 50 * i), 200);


            this.add(theirAttackPanels[i]);
        }
        alreadyAttacked = true;

        doAttacks();
        checkDead();

        this.revalidate();
        this.repaint();
    }

    public void createNewStatsTable(){
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            // indexes
            // 1 - id, 2 - name, 3 - hp, 4 - maxHp, 5 - team

            statment.execute("CREATE TABLE IF NOT EXISTS stats (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30), Hp INT, maxHp INT, team INT)");
            statment.execute("TRUNCATE TABLE stats RESTART IDENTITY"); // clears all previos data
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayAllStats(){
        // this will display all of the infomation in the stats DB
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            ResultSet rs = statment.executeQuery("SELECT id, name, Hp, maxHp, team FROM stats");

            System.out.println("\n databace stats :\n\n");
            while(rs.next()){
                System.out.println("id: " + rs.getInt("id") +" | name : " + rs.getString("square") +" | Hp : " +rs.getInt("Hp") +"  | maxHp : " + rs.getInt("maxHp") + " | team : " + rs.getInt("team"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void setAttacks(){
        for (int i = 0; i<6;i++){
            theirAttackOrder[i] = random.nextInt(1,4);
        }

    }

    public void enableAttacks(){
        parryAttack.setEnabled(true);
        normalAttack.setEnabled(true);
        hevyAttack.setEnabled(true);
    }

    public void doAttacks(){
        while (hpYours > 0 && hpTheirs >0) {
            for (int i = 0; i < 6; i++) {
                if (yourAttackOrder[i] == theirAttackOrder[i]) {
                    hpYours -= 1;
                    hpTheirs -= 1;
                    System.out.println("draw");
                } else if ((yourAttackOrder[i] - theirAttackOrder[i] + 3) % 3 == 2) {
                    hpTheirs -= strYours;
                    System.out.println("ypu win");
                } else if ((yourAttackOrder[i] - theirAttackOrder[i] + 3) % 3 == 1) {
                    hpYours -= strTheirs;
                    System.out.println("you lose");
                } else {
                    System.out.println("else");
                }

            /*
            // alternitve method for finding who won each round
            else if (yourAttackOrder[i] == 3 && theirAttackOrder[i] == 1) {
                System.out.println("you win");
                hpTheirs -= strYours;
            }
            else if (yourAttackOrder[i] == 2 && theirAttackOrder[i] == 3) {
                System.out.println("you win");
                hpTheirs -= strYours;
            }
            else if (yourAttackOrder[i] == 1 && theirAttackOrder[i] == 2) {
                System.out.println("you win");
                hpTheirs -= strYours;
            }
            else if (yourAttackOrder[i] == 1 && theirAttackOrder[i] == 3) {
                System.out.println("you loose");
                hpYours -= strTheirs;
            }
            else if (yourAttackOrder[i] == 3 && theirAttackOrder[i] == 2) {
                System.out.println("you loose");
                hpYours -= strTheirs;
            }
            else if (yourAttackOrder[i] == 2 && theirAttackOrder[i] == 1) {
                System.out.println("you loose");
                hpYours -= strTheirs;
            }
            */

                resetHpStats();
            }
            break;
        }
        checkDead();
    }

    public int getHpYours(){return hpYours;}

    public int getHpTheirs(){return hpTheirs;}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()== parryAttack){
            //hpTheirs -= (int)(Math.floor(0.5 * strYours));
            System.out.println("rock");
            yourAttackOrder[yourAttackEnd] = 1;
            JLabel label = new JLabel();
            yourAttackPanels[yourAttackEnd] = (new JPanel());

            label.setText("rocl");

            yourAttackPanels[yourAttackEnd].setSize(50,50);
            yourAttackPanels[yourAttackEnd].add(label);
            yourAttackPanels[yourAttackEnd].setLocation((100+ 50 * yourAttackEnd), 250);
            yourAttackEnd++;
            this.add(yourAttackPanels[yourAttackEnd]);
        }
        else if(e.getSource() ==normalAttack){
            //hpTheirs -= (int)(1 * strYours);
            System.out.println("paper");
            yourAttackOrder[yourAttackEnd] = 2;

            JLabel label = new JLabel();
            yourAttackPanels[yourAttackEnd] = (new JPanel());

            label.setText("paper");

            yourAttackPanels[yourAttackEnd].setSize(50,50);
            yourAttackPanels[yourAttackEnd].add(label);
            yourAttackPanels[yourAttackEnd].setLocation((100+ 50 * yourAttackEnd), 250);
            this.add(yourAttackPanels[yourAttackEnd]);
            yourAttackEnd++;
        }
        else if(e.getSource()== hevyAttack){
            //hpTheirs-= (int)(Math.floor(1.5 * strYours));
            System.out.println("scisior");
            yourAttackOrder[yourAttackEnd] = 3;

            JLabel label = new JLabel();
            yourAttackPanels[yourAttackEnd] = (new JPanel());

            label.setText("scisaors");

            yourAttackPanels[yourAttackEnd].setSize(50,50);
            yourAttackPanels[yourAttackEnd].add(label);
            yourAttackPanels[yourAttackEnd].setLocation((100+ 50 * yourAttackEnd), 250);
            this.add(yourAttackPanels[yourAttackEnd]);
            yourAttackEnd++;
        }
        else if(e.getSource()== showAttacks){
            makeAttackGrid();
        }

        if (yourAttackEnd == 6){
            parryAttack.setEnabled(false);
            normalAttack.setEnabled(false);
            hevyAttack.setEnabled(false);
            showAttacks.setEnabled(true);
        }

        checkDead();
        resetHpStats();

    }
}
