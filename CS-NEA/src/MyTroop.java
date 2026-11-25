import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MyTroop extends JPanel {


    int hp;
    int hpMax;
    int str;
    String name;
    int hight = 5;
    int width = 5;
    boolean isDead = false;
    int index;
    final String DB_URL = "jdbc:h2:./my_database";
    final String DB_USER = "";
    final String DB_PASSWORD = "";

    MyTroop (int id){
        index = id;

        setStats();

        this.setBackground(Color.YELLOW);
        this.setBounds(1,1 ,width, hight);
    }


    public boolean isDead(){ return isDead; }
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
    public String getName(){ return name; }
    private void setStats(){

        // gets all the stats for the troop and saves them to the MyTroop object

        // 1 - id, 2 - name, 3 - hp, 4 - maxHp, 5 - str, 6 - x (square), 7 - y (row), 8 - team

        String sql = "SELECT name, hp, maxHp, str FROM stats WHERE id = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sql)) {

            statment.setInt(1, index);
            statment.execute();

            ResultSet rs = statment.executeQuery();
            rs.next();
            name = rs.getString("name");
            hp = rs.getInt("hp");
            hpMax = rs.getInt("maxHp");
            str = rs.getInt("str");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
