import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MyFrame extends JFrame implements ActionListener {
    JFrame frame;
    JLayeredPane layeredPane;
    JButton button;
    JLayeredPane bottomPane;
    ArrayList<MyPanel> panels = new ArrayList<MyPanel>();
    MyPanel panel;
    UpButton upButton;
    UpButton downButton;
    LeftButton leftButton;
    LeftButton rightButton;
    int panelNum = 0;
    int panelMax = 0;
    JComboBox boxSelect;
    boolean moveButtons = false;
    JComboBox selectBoxType = new JComboBox(new Integer[] {1,2,3});



    MyFrame(){
        frame = new JFrame();
        button = new JButton("add box");
        layeredPane = new JLayeredPane();
        bottomPane = new JLayeredPane();
        layeredPane.setLayout(null);
        upButton = new UpButton("up");
        downButton= new UpButton("down");
        leftButton = new LeftButton("left");
        rightButton = new LeftButton("right");
        JLayeredPane buttonPane = new JLayeredPane();
        buttonPane.setLayout(new BorderLayout());


        Integer[] templist = {};
        boxSelect = new JComboBox(templist);
        boxSelect.addActionListener(this);
        boxSelect.setEnabled(false);

        selectBoxType.addActionListener(this);

        bottomPane.setLayout(new BorderLayout());
        bottomPane.setPreferredSize(new Dimension(75,75));

        frame.setSize(500,500);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        button.setSize(30,10);
        button.addActionListener(this);
        bottomPane.add(button, BorderLayout.EAST);

        upButton.addActionListener(this);
        downButton.addActionListener(this);
        leftButton.addActionListener(this);
        rightButton.addActionListener(this);


        JLayeredPane upDown = new JLayeredPane();
        upDown.setLayout(new BorderLayout());

        buttonPane.add(leftButton, BorderLayout.WEST);
        buttonPane.add(rightButton, BorderLayout.EAST);
        upDown.add(upButton, BorderLayout.NORTH);
        upDown.add(downButton, BorderLayout.SOUTH);

        buttonPane.add(upDown, BorderLayout.CENTER);

        buttonPane.setPreferredSize(new Dimension(150,75));

        bottomPane.add(boxSelect, BorderLayout.WEST);

        bottomPane.add(buttonPane, BorderLayout.CENTER);
        frame.add(bottomPane, BorderLayout.SOUTH);
        frame.add(layeredPane, BorderLayout.CENTER);


        frame.setVisible(true);
    }
    public void fillPane(){
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        upButton.setEnabled(moveButtons);
        downButton.setEnabled(moveButtons);
        leftButton.setEnabled(moveButtons);
        rightButton.setEnabled(moveButtons);

        if (e.getSource() == button){

            if( selectBoxType.getSelectedIndex() == 0){
                panels.add(new MyPanel(20,20,20,20, Color.RED));
            }
            if( selectBoxType.getSelectedIndex() == 1){
                panels.add(new MyPanel(20,20,20,20, Color.RED));
            }

            panel = panels.getLast();

            layeredPane.add(
                    panel,
                    JLayeredPane.DEFAULT_LAYER);


            moveButtons = true;
            button.setEnabled(false);
            panelMax += 1;
            panelNum = panelMax;
            boxSelect.addItem(panelMax);
            boxSelect.setEnabled(false);

        }
        if (e.getSource()== upButton){
            panel.movePane(0, -20);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        if(e.getSource() == downButton){
            panel.movePane(0, 20);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        if (e.getSource()== leftButton){
            panel.movePane(-20, 0);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        if(e.getSource()== rightButton){
            panel.movePane(20, 0);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        if(e.getSource()== boxSelect && button.isEnabled()){
            panel = panels.get(boxSelect.getSelectedIndex());
        }
    }
}
