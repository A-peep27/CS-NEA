import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class MyFrame extends JFrame implements ActionListener , MouseListener{
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
    int[][] map;
    JButton fillButton;
    int width = 500;
    int hight = 500;
    boolean madeMap = false;
    int[] squareSize;
    int [] mouseLocation = new int[2];
    int clickCount = 0;
    int[] startEnd = new int[3];
    final String DB_URL = "jdbc:h2:./my_database";
    final String DB_USER = "";
    final String DB_PASSWORD = "";
    int maxPathLength = 100;
    int pathFinding = 0;
    JButton pathFindingToggel;
    ArrayList<MyTroop> troops = new ArrayList<MyTroop>();
    MyTroop troop;
    ArrayList<int[]> roomSquares = new ArrayList<int[]>();
    boolean[][] usedRoomGrid;
    int[] teamsOnBoard = new int[3];
    Random random = new Random();
    int[][] teamBace = new int[3][3];


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
        fillButton = new JButton();

        fillButton.setBounds(((int)(width/2) - 50),((int)(hight/2) -50),100,50);
        fillButton.setText("fill grid");
        fillButton.addActionListener(this);
        frame.add(fillButton);

        Integer[] templist = {};
        boxSelect = new JComboBox(templist);
        boxSelect.addActionListener(this);
        boxSelect.setEnabled(false);

        selectBoxType.addActionListener(this);

        bottomPane.setLayout(new BorderLayout());
        bottomPane.setPreferredSize(new Dimension(75,75));

        frame.setSize( width, hight);
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
        bottomPane.setVisible(false);

        frame.add(layeredPane, BorderLayout.CENTER);

        pathFindingToggel = new JButton();
        pathFindingToggel.addActionListener(this);
        pathFindingToggel.setBounds(width - 100, 10, 75, 30);

        pathFindingToggel.setText("path finding");
        layeredPane.add(pathFindingToggel, JLayeredPane.DEFAULT_LAYER);

        frame.setVisible(true);

        createNewPanelTable();
        createNewStatsTable();
        createNewBaseTable();
    }

    //map display function
    public void fillGrid(){
        long timeStart = System.nanoTime();
        int gridHight = 50;     // making the x and y bigger will slow down all processing todoa with the map
        int gridWidth = 50;     // like path finding or generation
        MapMaker mapMaker = new MapMaker( gridHight, gridWidth); // creates a 2D array of 1s and 0s with the gridhight and with as the max for rows and cols of the grid
        int tryCount = 0;
        usedRoomGrid = new boolean[gridHight][gridWidth];
        try {
            while (mapMaker.getCount() <= 500) { // makes a grid with at least this many squares
                mapMaker.fillGrid();
                tryCount++;
                System.out.println("map made");
            }

            mapMaker.cullGrid(); // removes empty rows and colembs this is so the map is a resoable size
            //mapMaker.makeMapFull(); // makes it into one large ssquare for testing

            System.out.println(tryCount);
            if (madeMap) { // removes the squares if makeing a second grid on top
                for (int i = 0; i < (panelMax - 1); i++) {
                    frame.remove(panels.get(i));
                    System.out.println("removed");
                    layeredPane.remove(panels.get(i));
                }
                panels.clear();
                panelMax = 0;
                frame.revalidate();
                frame.repaint();
            }


            map = mapMaker.getGrid(); // gets the grid
            long timeEnd = System.nanoTime();
            System.out.println("time to make grid : " + (double)(timeEnd - timeStart)/1000000000 + "s \n");

            gridWidth = map[0].length;
            gridHight = map.length;     // using varables to make some steps simpler and faster
            timeStart = System.nanoTime();
            createNewPanelTable();
            String sqlStatment = "INSERT INTO panels (row_num, square, x, y) VALUES (?, ?, ?, ?)";
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statment = con.prepareStatement(sqlStatment)) {

                 squareSize = new int[]{((int) ((hight - 100) / gridHight)), ((int) ((width - 100) / gridWidth))};
                 for (int row = 0; row < gridHight; row++) {
                    for (int square = 0; square < gridWidth; square++) {
                        if (map[row][square] == 1) { // if there is a one in that location panins  a square in the corosponding spot on the frame
                            usedRoomGrid[row][square] = true;
                            panels.add(new MyPanel(squareSize[1] * (square + 1), squareSize[0] * (row + 1), squareSize[1], squareSize[0], Color.BLACK));
                            panel = panels.getLast();

                            // adds the infomation of the square on to a DB for later use
                            statment.setInt(1, row);
                            statment.setInt(2, square);
                            statment.setInt(3, squareSize[1] * (square + 1));
                            statment.setInt(4, squareSize[0] * (row + 1));

                            statment.execute();

                            panel.addMouseListener(this);

                            // adds to the frame
                            layeredPane.add(
                                    panel,
                                    JLayeredPane.DEFAULT_LAYER);

                            panelMax++;
                            boxSelect.addItem(panelMax);
                        } else {
                            usedRoomGrid[row][square] = false; // although this will be false by defalt its better to be safe and asign it
                        }
                    }
                }
            }

            timeEnd = System.nanoTime();

            System.out.println("time to dispaly grid : " + (double)(timeEnd - timeStart)/1000000000 + "s \n");

            frame.revalidate();
            frame.repaint();

            JLabel label = new JLabel(); // dispays the number of squares on screen
            label.setText("" + panelMax);
            label.setFont(new Font("MV Boli", Font.PLAIN, 15));
            JPanel panel = new JPanel();//10,10,50,50, Color.WHITE
            panel.setBounds(10, 10, 50, 30);
            panel.setBackground(Color.white);
            panel.add(label);
            layeredPane.add(panel, 2);

            madeMap = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //DB functions
    //-DB creation
    public void createNewPanelTable() { // ensures that there is a table for the panels
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            // indexes
            // 1 - id, 2 - row_num, 3 - square, 4 - x, 5 - y
            statment.execute("CREATE TABLE IF NOT EXISTS panels (id INT AUTO_INCREMENT PRIMARY KEY, row_num INT, square INT, x INT, y INT)");
            statment.execute("TRUNCATE TABLE panels RESTART IDENTITY"); // clears all previos data
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void createNewStatsTable(){
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            // indexes
            // 1 - id, 2 - name, 3 - hp, 4 - maxHp, 5 - str, 6 - x (square), 7 - y (row), 8 - team
            // for this we are going to say that team '1' is the players team

            statment.execute("CREATE TABLE IF NOT EXISTS stats (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30), Hp INT, maxHp INT, str INT, x INT, y INT, team INT)");
            statment.execute("TRUNCATE TABLE stats RESTART IDENTITY"); // clears all previos data
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void createNewBaseTable(){
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            // indexes
            // 1 - id, 2 - team, 3 - x, 4 - y, 5 - type
            // for this we are going to say that team '1' is the players team

            statment.execute("CREATE TABLE IF NOT EXISTS bases (id INT AUTO_INCREMENT PRIMARY KEY, team INT, x INT, y INT, type INT)");
            statment.execute("TRUNCATE TABLE bases RESTART IDENTITY"); // clears all previos data
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //-DB display (these two functions may not be used but they are to check data while making the project)
    public void displayAllSquareLocations(){
        // this will display all of the infomation in the panels DB
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            ResultSet rs = statment.executeQuery("SELECT id, x, y, square, row_num FROM panels");

            System.out.println("\n databace panels :\n\n");
            while(rs.next()){
                System.out.println(
                                "id: " + rs.getInt("id") +" | square : " + rs.getInt("square") +
                                " | row : " + rs.getInt("row_num") +
                                "  | x : " + rs.getInt("x") + " | y : " + rs.getInt("y"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void displayAllStats(){
        // this will display all of the infomation in the stats DB
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            // 1 - id, 2 - name, 3 - hp, 4 - maxHp, 5 - str, 6 - team

            ResultSet rs = statment.executeQuery("SELECT id, name, Hp, maxHp, str, team, x, y FROM stats");

            System.out.println("\n databace stats :\n\n");
            while(rs.next()){
                System.out.println("id: " + rs.getInt("id") +" | name : " + rs.getString("name") +
                        " | Hp : " +rs.getInt("Hp") +"  | maxHp : " + rs.getInt("maxHp") +
                        " | str : " + rs.getInt("str") + " | team : " + rs.getInt("team") +
                        " | x : " + rs.getInt("x") + " | y : " + rs.getInt("y"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void displayAllBases(){
        // this will display all of the infomation in the bases DB
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statment = con.createStatement()) {

            // 1 - id, 2 - team, 3 - x, 4 - y, 5 - type

            ResultSet rs = statment.executeQuery("SELECT id, team, x, y, type, team FROM bases");

            System.out.println("\n databace stats :\n\n");
            while(rs.next()){
                System.out.println("id: " + rs.getInt("id") +" | team : " + rs.getInt("team") +
                        " | x : " +rs.getInt("x") +"  | y : " + rs.getInt("y") +
                        " | type : " + rs.getInt("type")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //-DB adding data
    public void addToPanelTable(int rwo_num, int square, int x, int y){
        // adds to the panles DB
        // 1 - id, 2 - row_num, 3 - square, 4 - x, 5 - y
        String sqlStatment = "INSERT INTO panels (row_num, square, x, y) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sqlStatment)) {

            statment.setInt(1, rwo_num);
            statment.setInt(2, square);
            statment.setInt(3, x);
            statment.setInt(4, y);

            statment.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void addToStatsTable(String name, int hp, int str, int team, int x, int y){
        // adds sothing to the stats DB
        // 1 - id, 2 - name, 3 - hp, 4 - maxHp, 5 - str, 6 - x (square), 7 - y (row), 8 - team
        String sqlStatment = "INSERT INTO stats (name, hp, maxHp, str, x, y, team) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sqlStatment)) {

            statment.setString(1, name);
            statment.setInt(2, hp);
            statment.setInt(3, hp);
            statment.setInt(4, str);
            statment.setInt(5, x);
            statment.setInt(6, y);
            statment.setInt(7, team);

            statment.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void addToBaseTable(int team, int x, int y, int type){
        // 1 - id, 2 - team, 3 - x, 4 - y, 5 - type
        String sqlStatment = "INSERT INTO bases (team, x, y, type) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sqlStatment)) {

            statment.setInt(1, team);
            statment.setInt(2, x);
            statment.setInt(3, y);
            statment.setInt(4, type);

            statment.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //map editing functions
    public void setEditing(){
        bottomPane.setVisible(true);
        upButton.setEnabled(true);
        upButton.setVisible(true);
        downButton.setEnabled(true);
        downButton.setVisible(true);
        leftButton.setEnabled(true);
        leftButton.setVisible(true);
        rightButton.setEnabled(true);
        rightButton.setVisible(true);
        bottomPane.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }
    public void stopEditing(){
        bottomPane.setVisible(false);
        upButton.setEnabled(false);
        upButton.setVisible(false);
        downButton.setEnabled(false);
        downButton.setVisible(false);
        leftButton.setEnabled(false);
        leftButton.setVisible(false);
        rightButton.setEnabled(false);
        rightButton.setVisible(false);
        bottomPane.setVisible(false);
        frame.revalidate();
        frame.repaint();
    }
    public void resetAllColors(){
        // resets all of the squares on screens color to their default
        MyPanel panel;
        for (int i = 0; i < panelMax; i++){
            panel = panels.get(i);
            panel.resetColor();
        }
    }


    // path finding functions
    public int findSquare(int square, int row){
        // finds the index of the sqaure using its x,y on scrren

        // now works quite well wont make future errors (hopefully)
        int count = 0;

        String sql = "SELECT id FROM panels WHERE square = ? AND row_num = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sql)) {

            statment.setInt(1, square);
            statment.setInt(2, row);
            statment.execute();

            ResultSet rs = statment.executeQuery();
            rs.next();
            count = rs.getInt("id") - 1;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("x : " + square + ", y : " + row);
            //displayAllSquareLocations();
        }


        return count;
    }
    public int[] findGridLocation(int index){
        // finds the row and colomn of the sqaure on the grid
        // i could turn this into part of the data bace later to speed it up but thats a futer me problem
        // it works less accuratly with the DB for some reason

        int row = 0;
        int colemb = 0;
        MyPanel panel = panels.get(index);

        int x = panel.getXY()[0];
        int y = panel.getXY()[1];

        if(x!= 0) {
            colemb = x / squareSize[1] -1;
        }
        if(y!= 0) {
            row = y / squareSize[0] -1;
        }
        return new int[] {colemb, row};
    }
    public boolean isMouseOnASquare(int x, int y){
        if (map[(int) (double) Math.floor(y/squareSize[0])][(int) (double) Math.floor(x/squareSize[1])]==1){
            return true;
        }
        else{
            return false;
        }
    }
    public double findDist (int[] a, int[] b){ // find the distance betweeen to points
        double dist = 0;

        int xDist = Math.abs(a[0] - b[0]);
        int yDist = Math.abs(a[1] - b[1]);

        dist = (double) Math.floor(Math.sqrt(xDist*xDist + yDist*yDist));

        return dist;
    }
    public boolean[] checkSurroundings(int x, int y){ //checks which of the 4 surrounding sqaurs are a square
        boolean[] free = new boolean[]{false, false, false, false};
        // the if statments before is to ensure that we dont go out of the grid when pathfinding
        if (y!=(map.length-1)) {
            if (map[y + 1][x] == 1) {
                free[0] = true;
            }
        }
        if (x!=(map[0].length-1)) {
            if (map[y][x + 1] == 1) {
                free[1] = true;
            }
        }
        if(y!=(0)) {
            if (map[y - 1][x] == 1) {
                free[2] = true;
            }
        }
        if(x!=0) {
            if (map[y][x - 1] == 1) {
                free[3] = true;
            }
        }

        return free;
    }

    public int[] findPath(int a, int b, int pathLength){
        // this is me trying to make the searching algorithum
        // this ended up being gready best frend esque serch so it can get stuck as it wont go back on its self
        // we could fix this later by making it closer to an a* path finding but that would be slower
        // as it does more calulations per square that it goes on which currently we dont want

        MyPanel panelA = panels.get(a); // we start by getting the two panesl that we want to find th path between
        MyPanel panelB = panels.get(b);

        double minDist = 0;
        int[] gridIndexPath = new int[maxPathLength]; // we use this to track what squares the path goes on

        int[] aGridLocate = findGridLocation(a); // then i get the loaction on the grid that is displayed
        int[] bGridLocate = findGridLocation(b);

        if (pathLength <= 0) {
            panelA.changeColor(Color.MAGENTA);      // now we change the start color tomagenta  so it can be spotted
            panelA.setDefualtColor(Color.MAGENTA);
            panelB.changeColor(Color.CYAN);         // we do the same to the end but in cycan
            panelB.setDefualtColor(Color.CYAN);
        }

        minDist  = findDist(aGridLocate, bGridLocate); // we then find the minimum sistance as the crow flys between the two point

        boolean foundDestiantion = false;

        int[] currentSquare = new int[] {aGridLocate[0], aGridLocate[1]};
        int itterationNum = 0;

        boolean[][] visited = new boolean[map.length][map[0].length]; // i though a boolean would be better as i olny end up using 0s and 1s for ints

        // track prevoius
        long timeStart = System.nanoTime();
        while(!foundDestiantion && itterationNum < maxPathLength){
            // here is bing slow need to speed up its about .02 s per square
            // and there is a max of 100 so 2s MAX per path find which is too long
            // so we could either reduce max distance to what the movemtn disdtance is
            // or speed it up which would be prefrable

            visited[currentSquare[1]][currentSquare[0]] = true;

            boolean[] suroundings = checkSurroundings(currentSquare[0], currentSquare[1]);
            double tempDist = Double.MAX_VALUE;
            int choice = 4; // as 4 cant be chosen it shows that the square is stuck so we set it to that


            // find the shortest distance to dest
            if(suroundings[0] && !visited[currentSquare[1]+1][currentSquare[0]]){
                // checks that there is a square there free usi=eing suoundings and then verifys that square hasnt been
                // visited using the visited 2D array
                tempDist = findDist(new int[]{currentSquare[0], currentSquare[1]+1}, bGridLocate);
                choice = 0;
            }

            if(suroundings[1]&& !visited[currentSquare[1]][currentSquare[0]+1]){
                if (findDist(new int[]{currentSquare[0]+1, currentSquare[1]}, bGridLocate)< tempDist) {
                    tempDist = findDist(new int[]{currentSquare[0]+1, currentSquare[1]}, bGridLocate);
                    choice = 1;
                }
            }
            if(suroundings[2] && !visited[currentSquare[1]-1][currentSquare[0]]){
                if (findDist(new int[]{currentSquare[0], currentSquare[1]-1}, bGridLocate)< tempDist) {
                    tempDist = findDist(new int[]{currentSquare[0], currentSquare[1]-1}, bGridLocate);
                    choice = 2;
                }
            }
            if(suroundings[3] && !visited[currentSquare[1]][currentSquare[0]-1]){
                if (findDist(new int[]{currentSquare[0]-1, currentSquare[1]}, bGridLocate)< tempDist) {
                    tempDist = findDist(new int[]{currentSquare[0]-1, currentSquare[1]}, bGridLocate);
                    choice = 3;
                }
            }



            if(choice == 0){ currentSquare[1]++; } // changes what the current squares index is
            else if(choice == 1){ currentSquare[0]++; }
            else if(choice == 2){ currentSquare[1]--; }
            else if(choice == 3){ currentSquare[0]--; }
            else if(choice == 4){
                System.out.println("all surrouding squares used");
                break;
            }


            if (currentSquare[0] == bGridLocate[0] && currentSquare[1] == bGridLocate[1]){ // if its at the end the x and y need to be the same
                foundDestiantion = true;

            } else {
                int targetSquareIndex = findSquare(currentSquare[0], currentSquare[1]);
                if(targetSquareIndex >= 0 && targetSquareIndex < panels.size()){
                    gridIndexPath[itterationNum] = targetSquareIndex; // this is so we can display the path later
                }
            }
            itterationNum++;
        }

        if (pathLength <= 0 ) {
            // we pass the path length varable through to also see if we wanted to use the function for testing
            // as id its 0 or less we show the hole path and display it which alows for testing
            if (foundDestiantion){
                System.out.println("found a  path");
            }
            else{
                System.out.println("not path found");
            }
            long timeEnd = System.nanoTime();
            System.out.println("time to find path : " + (double) (timeEnd - timeStart) / 1000000000 + "s \n");
            System.out.println("time per square : " + (double) ((timeEnd - timeStart) / 1000000000) / itterationNum + "s \n");
            timeStart = System.nanoTime();

            for (int i = 0; i < itterationNum; i++) {
                panel = panels.get(gridIndexPath[i]);
                panel.changeColor(Color.GREEN);
            }
            timeEnd = System.nanoTime();
            System.out.println("time to display path : " + (double) (timeEnd - timeStart) / 1000000000 + "s \n");
            System.out.println("done");
        }
        else {
            // this uses the path length to retun a path of that length
            int[] returnPath = new int[pathLength];
            for (int i = 0; i < itterationNum && i < pathLength; i++) {
                returnPath[i] = gridIndexPath[i];
            }
            return returnPath;
        }

        return new int[]{};
    }

    // getting inputs and type validation
    public String getInput(String q){
        String ans = "";

        StatsInput statsInput = new StatsInput(frame, q);
        statsInput.setVisible(true);

        ans = statsInput.result;

        return ans;
    }
    public boolean isInt(String str){
        // is cheking to see if we can cast the value to an int
        int len = str.length();
        if (str == null){
            // it cant be an int if its null
            return false;
        }
        else if( len == 0){
            // has to have a length to be an number
            return false;
        }
        else {
            // i could check if its negative but we dont want those so we dont look for them
            for(int i = 0; i < len; i++){
                char cha = str.charAt(i);
                if ( cha < '0' || '9' < cha ){  // we do this as we want it to be between 0 and 9 so '0 < cha < 9'
                                                // so we just do it the other way around where it would be false
                    return false;
                }
            }
        }
        return true;
    }

    // making a room
    /*
        easy part:
            so first part of this wont be to hard to make as we opnly need to change the color if a ssquare and give it an atribute
            then we dispaly it
            done
        harder part
            make it do stuff :(
            so what do we want ti to do?
            so firts we want just like coridoors simple change the color of a SET of squares
            yes thats it we make it complicated by changing a set of them in a pattern like a corridor (stight (bends latter)
                 later : we could have diffrent colors do diffrent things like entrences or traps
            then we alow for the addition of troops to rooms
            this can the nalow for us to make rooms do things to troops portentialy linking it to color
     */
    //room functions
    public boolean isARoomThere (int x, int y){
        // 1 - id, 2 - team, 3 - x, 4 - y, 5 - type
        String sql = "SELECT 1 FROM bases WHERE x = ? AND y = ? LIMIT 1";
        int id = 0;

        // uses the DB to see if there is a room in that location

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sql)) {

            statment.setInt(1, x);
            statment.setInt(2, y);

            try(ResultSet rs = statment.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void makePlayerRoom(int gridLocation){

        /*
        int team = 2;
        int hight = map.length;
        int width = map[0].length;
        boolean teamOnScreen = false;
        for (int i = 0; i < teamsOnBoard.length; i++){
            if (team == i){
                teamOnScreen = true;
                break;
            }
        }

        // initolizes all things to do with making a new team on the grid
        if (!teamOnScreen){
            int x = 0;
            int y = 0;
            while (!usedRoomGrid[y][x]){
                x = random.nextInt(width);
                y = random.nextInt(hight);
            }

            // we now make it fill an area like 11x11 around square on usedRoomGrid false so no new teams in said area

            // getting the x and y max and min values for the area that we dont want new teams to
            // spawn on the screen
            int minX = x-5;
            int maxX = x+5;
            while (minX < 0){
                minX++;
            }
            while (maxX > map[0].length){
                maxX--;
            }

            int minY = y-5;
            int maxY = y +5;
            while (minY < 0){
                minY++;
            }
            while (maxY > map.length){
                minY--;
            }

            // now we make ALL values in the 11x11 grid false, we can do this as it would either be T or F so we can
            // just make them all Fasle
            for(int row = minY; row  < maxY; row++){
                for (int colem = minX; colem < maxX; colem++){
                    usedRoomGrid[row][colem] = false;
                }
            }

            teamBace[team] = new int[] {team, x, y};
            addToBaseTable(team, x, y, 1);

            int gridLocation2 = findSquare(x, y);
            MyPanel panel = panels.get(gridLocation2);
            panel.setDefualtColor(Color.PINK);
            panel.resetColor();
        }



        MyPanel panel = panels.get(gridLocation);
        panel.setDefualtColor(Color.RED);
        int[] xy = findGridLocation(panels.indexOf(panel));
        usedRoomGrid[xy[1]][xy[0]] = false;
        panel.resetColor();
         */

        addEnemyRoom(2);
        //System.out.println("player room made");
    }
    public int checkSurroundingsUsed(int x, int y, boolean[][] areaFill){

        // alows me to see which of the 8 surounding sqares are used i needed a second funtion for this as the checkSurroundings as that only
        // checks 4 rooms
        int free = 0;
        if (y!=(areaFill.length-1)) {
            if(x!=0) {
                if (areaFill[y + 1][x - 1]) {
                    free++;
                }
            }
            if (areaFill[y + 1][x]) {
                free++;
            }
            if (x!=(areaFill[0].length-1)) {
                if (areaFill[y + 1][x + 1]) {
                    free++;
                }
            }
        }
        if (x!=(areaFill[0].length-1)) {
            if (areaFill[y][x + 1]) {
                free++;
            }
        }
        if(y!=(0)) {
            if(x!=0) {
                if (areaFill[y - 1][x - 1]) {
                    free++;
                }
            }
            if (areaFill[y - 1][x]) {
                free++;
            }
            if (x!=(areaFill[0].length-1)) {
                if (areaFill[y - 1][x + 1]) {
                    free++;
                }
            }
        }
        if(x!=0) {
            if (areaFill[y][x - 1]) {
                free++;
            }
        }
        return free;
    }
    public void addEnemyRoom(int team) {
        // okay so first we want a square
        /* how do we chose?
                1. random (boring)
                2. give a set area that one can spawn (like the roooooooms)
                3. place it (this would be a temporay solution so its bad in long term so yeah)
            we going to try 2
                okay so now how do we make these rooms spawn
                1. random (bit better for this but still boring as it could be any where)
                2. set area random (good but nah we know how bad radomly cjosing a square can be rom map making)
                3. before displaying the map we chose an area that we can make an enemy room and then
                    put that in a DB or an array ( better but seams inifishent)
                4. using a 2d array see where i can put the enimy giving areas where it cant spawn (like 10 pixels
                    arround the player) and remove parts from the array to make more teams unitll not enough space
                    is left or a max enemy count
            ill attempt 4 but as it might not work ill then mix in 2/3 if it falls through
            now where do we put the 2D array as i could either make a 2D local array and for this we might need 2
            no we can do this with one extra for where new team rooms/baces can be placed
         */
        // so first we need to see if that team is on the board(currently we have a three team max so two enemy teams

        if(team > teamsOnBoard.length){
            System.out.println("not a vaild team");
            return;
        }

        int hight = map.length;
        int width = map[0].length;
        boolean teamOnScreen = false;
        for (int i = 0; i < teamsOnBoard.length; i++){
            if (team == teamsOnBoard[i]){
                teamOnScreen = true;
                break;
            }
        }

        int bounds = 11;
        int halfBound = (int) Math.floor(bounds/2); // i also use this just so there is less repeated calulations and it looks nice

        // initolizes all things to do with making a new team on the grid
        if (!teamOnScreen){
            int x = random.nextInt(0, hight);
            int y = random.nextInt(0, width);
            while (!usedRoomGrid[y][x]){
                x = random.nextInt(0, width);
                y = random.nextInt(0, hight);
            }

            System.out.print("x : " + x + "| y : " + y);
            // we now make it fill an area like 11x11 around square on usedRoomGrid false so no new teams in said area

            // getting the x and y max and min values for the area that we dont want new teams to
            // spawn on the screen
            int minX = x- halfBound;
            int maxX = x+ halfBound;
            while (minX < 0){
                minX++;
            }
            while (maxX > map[0].length){
                maxX--;
            }

            int minY = y- halfBound;
            int maxY = y + halfBound;
            while (minY < 0){
                minY++;
            }
            while (maxY > map.length){
                minY--;
            }

            // now we make ALL values in the 11x11 grid false, we can do this as it would either be T or F so we can
            // just make them all Fasle
            for(int row = minY; row  < maxY; row++){
                for (int colem = minX; colem < maxX; colem++){
                    usedRoomGrid[row][colem] = false;
                }
            }

            teamBace[team] = new int[] {team, x, y};
            addToBaseTable(team, x, y, 1);

            int gridLocation = findSquare(x, y);
            MyPanel panel = panels.get(gridLocation);
            panel.setDefualtColor(Color.PINK);
            panel.resetColor();
            teamsOnBoard[team] = team;
            System.out.println("| index  : " + gridLocation);
        }
        // this will now do things for if that team IS on the grid
        else{

            // we find the main base
            int baceX = -1;
            int baseY = -1;
            for (int i = 0; i< teamBace.length; i++){
                if(teamBace[i][0] == team){
                    baceX = teamBace[i][1];
                    baseY = teamBace[i][2];
                }
            }
            System.out.println("main base x : " + baceX + "| y" + baseY);
            if(baseY ==-1 || baceX == -1){
                // if the main base isnt on screen we stop the function
                return;
            }

            // we now chose where to spawn a new base
            /*
            okay how?
                1. pure random in 11x11 area
                    - could work but becouse of how we made them spawn wouldnt be the best
                2. spreading out chosing a certain square to make the next like MapMaker
                    - better but still not the best as it would be a bit hard to implement
                3. we could do a wighted algortum for how far it goes in the x and y
                    - again better but it would be a bit boring and like 1 but with more steps
                4. we could fill the area with noice and make it do stuff with that
                    - again better but potentialy has to go through itterations
            i will try 4 and go from there as this might take a bit of testing
            we also might need to make a set of like 5 at a time brcouse of this this also alows for the player
            to make progress so its fine
             */

            // first we make the max area we fill
            long sTime = System.nanoTime();
            boolean[][] areaFill = new boolean[bounds][bounds];
            int maxFill = 10;           // then we set the max number of rooms we want
            int roomChance = 40;   // the chance to make a room (40% to 60% is probly a good range)

            /*  so what we will do is make all the squares have a chance to be T/F then for a particlar square
                we say if there is 1- a F, 2-3 sqaures around its a T, 4+ a F
                then we say from the center it will have a 1 garated as we already put something there */
            int[] fillList = new int[bounds*bounds];
            int counter = 0;
            // this will make a grid of noise that can be used for the generation
            for (int row= 0 ; row < bounds; row++){
                if(!((baseY - halfBound + row) < 0 || (baseY - halfBound + row) > map.length)) {
                    for (int colem = 0; colem < bounds; colem++) {
                        if (((baceX - halfBound + colem) > 0 || (baceX - halfBound + colem) < map[0].length)) {
                            if (random.nextInt(0, 100) < roomChance && map[baseY - halfBound + row][baceX - halfBound + colem] == 1) {
                                areaFill[row][colem] = true;
                                fillList[counter] =  (findSquare((baceX - halfBound + colem), (baseY - halfBound + row)));
                                counter++;
                            }
                        }
                    }
                }
            }
            long eTime = System.nanoTime();
            System.out.println("time to find noise : " + (double)(eTime - sTime)/1000000000 + "s \n");

            sTime = System.nanoTime();
            // then we display the noise for development refrense
            MyPanel myPanel;
            for(int i = 0; i< counter; i ++){
                myPanel = panels.get(fillList[i]);
                myPanel.changeColor(Color.LIGHT_GRAY);
            }
            eTime = System.nanoTime();
            System.out.println("time to show noise : " + (double)(eTime - sTime)/1000000000 + "s \n");

            // now we need to make an algorithum to turn noise to a map
            /*  so how will we make it ?
            idk i just like made it
             */
            // we could change it to a diffrent serch that goes from the middle and goes out in a spiral
            // so we can stop it at a set value
            counter = 0;
            int freeNum = 0;
            int value = 0;
            int[][] grid = new int[bounds][bounds];

            int[] moveDist = {1, 0, -1, 0};
            int xIndex = 0;
            int yIndex = 1;
            int upTo = 1;
            int row = halfBound;
            int colem = halfBound;

            while (value < bounds * bounds && counter < maxFill){
                for (int j =0; j < 2 && value < bounds * bounds; j++){
                    for(int i = 0; i < upTo && value < bounds * bounds; i ++){
                        value++;
                        colem += moveDist[xIndex];
                        row += moveDist[yIndex];
                        if (row < bounds && colem < bounds) {
                            if (areaFill[row][colem]) {
                                freeNum = checkSurroundingsUsed(colem, row, areaFill);
                                if (freeNum < 3) {
                                    // i know that this is a useless if statment but it makes the code make more sense
                                } else if (freeNum < 5 && freeNum >= 3) {
                                    if (!isARoomThere((baceX - halfBound + colem), (baseY - halfBound + row))) {
                                        counter++;
                                        fillList[counter] = (findSquare((baceX - halfBound + colem), (baseY - halfBound + row)));
                                    }
                                } else if (freeNum > 4) {
                                }
                            }
                        }
                        if (row >= 0 && row < bounds && colem >= 0 && colem < bounds) {
                            grid[row][colem] = value;
                        }
                    }
                    xIndex++;
                    if (xIndex > 3){
                        xIndex = 0;
                    }
                    yIndex++;
                    if (yIndex > 3){
                        yIndex = 0;
                    }
                }
                upTo++;
            }

            int[] xy;

            for(int i = 0; i< counter; i ++){
                myPanel = panels.get(fillList[i]);
                myPanel.changeColor(Color.ORANGE);
                xy = findGridLocation(fillList[i]);
                addToBaseTable(team, xy[0], xy[1], 1);
            }
            System.out.println(counter);
            displayAllBases();

            // okay so now that the rooms spawn we need to add this to the Database

        }
        System.out.println("enemy room made");
    }
    //troop functions
    public void moveTroop(int troopNum, int endSquare, int endRow) {
        MyTroop troop1 = troops.get(troopNum - 1);
        int x;
        int y;
        String sql = "SELECT x, y FROM stats WHERE id = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sql)) {

            statment.setInt(1, troopNum);
            statment.execute();

            ResultSet rs = statment.executeQuery();
            rs.next();
            x = rs.getInt("x");
            y = rs.getInt("y");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int panelId1 = findSquare(x, y);
        int panelId2 = findSquare(endSquare, endRow);
        int pathLen = 5;
        MyPanel panel = panels.get(panelId1);
        panel.remove(troop1);
        panel.revalidate();
        panel.repaint();

        int dest = findPath(panelId1, panelId2, pathLen)[pathLen-1];

        panel = panels.get(dest);
        panel.resetColor();
        panel.add(troop1);
        endSquare = findGridLocation(dest)[1];
        endRow = findGridLocation(dest)[0];


        sql = "UPDATE stats SET x = ?, y = ? WHERE id = ?";

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sql)) {

            statment.setInt(1, endSquare);
            statment.setInt(2, endRow);
            statment.setInt(3, troopNum);
            statment.execute();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isATroopThere (int x, int y){
        String sql = "SELECT 1 FROM stats WHERE x = ? AND y = ? LIMIT 1";
        int id = 0;

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statment = con.prepareStatement(sql)) {

            statment.setInt(1, x);
            statment.setInt(2, y);

            try(ResultSet rs = statment.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* okay so now we have most of the fnctions that we need we just need to make the whole thing itterate
    so how do we do this?
        - make rounds
        - make constand development
        - have the player go at their own pace and randomly have enemy devlopment
    ill go with rounds as this makes it alot easyer for me and makes the player have to think in a way of
    what to do next round and how that would impact stuff
    
     */


    @Override
    public void actionPerformed(ActionEvent e) {

        // okay os now i need to make the stat blocks work, this will also include alowing for troops to be
        // deployed which will halp make the progress

        /* okay now how can we do this
        method one :
            adapt the map moving buttons to work with the troops
            this wont be to much effort

         method two:
            link key binds and adapt the mouse links to move
            this could work but currently we dont need movment soooooo
            what we could do is just adapt the mouse to spawn troops on certain squares
            and make a togle button

         i did method 2 as it is quicker and ill need to change it later no matter what
         */

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
        else if (e.getSource() == fillButton){
            fillButton.setVisible(false);
            frame.remove(fillButton);
            fillGrid();
            //setEditing();
            moveButtons = true;
        }
        else if (e.getSource()== upButton){
            panel.movePane(0, -20);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        else if(e.getSource() == downButton){
            panel.movePane(0, 20);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        else if (e.getSource()== leftButton){
            panel.movePane(-20, 0);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        else if(e.getSource()== rightButton){
            panel.movePane(20, 0);
            button.setEnabled(true);
            boxSelect.setEnabled(true);
        }
        else if(e.getSource()== boxSelect && button.isEnabled()){
            panel = panels.get(boxSelect.getSelectedIndex());
        }
        else if(e.getSource() == pathFindingToggel){
            pathFinding++;
            clickCount = 0;
            resetAllColors();
            if (pathFinding >= 4 || pathFinding <= 0){
                pathFinding = 0;
            }
            if (pathFinding == 0){
                System.out.println("path finding");
                pathFindingToggel.setText("path finding");
            }
            else if (pathFinding == 1){
                System.out.println("adding troops ");
                pathFindingToggel.setText("adding troops");
            }
            else if (pathFinding == 2){
                System.out.println("adding rooms");
                pathFindingToggel.setText("adding rooms");
            }
            else if (pathFinding == 3){
                System.out.println("moving troop");
                pathFindingToggel.setText("moving troop");
            }
            this.revalidate();
            this.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        panel.changeColor(Color.GREEN);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pathFinding ==0 ) {
            clickCount++;
            if (clickCount <= 2) {
                if (e.getSource() == panel) {
                    if (panel.getColor() == Color.GREEN) {
                        panel.setDefualtColor(Color.ORANGE);
                        panel.resetColor();
                    }
                    if (clickCount == 1) {
                        startEnd[0] = panels.indexOf(panel);
                    } else if (clickCount == 2) {
                        startEnd[1] = panels.indexOf(panel);
                    }
                } else {
                    clickCount = 0;
                    resetAllColors();
                }
            }
            if (clickCount == 2) {
                findPath(startEnd[0], startEnd[1], -1);
            }
            if (clickCount==3){
                resetAllColors();
                clickCount = 0;
            }
        }
        else if (pathFinding == 1){
            String name = getInput("what is its name");
            while (name == "" || name == "what is its name"){
                name = getInput("what is its name");
            }
            String strHp = getInput("what is the HP");
            while (!isInt(strHp)){
                strHp = getInput("enter a nuber for hp");
            }
            int hp = Integer.valueOf(strHp);
            String strStrength = getInput("what is its srength");
            while (!isInt(strStrength)){
                strStrength = getInput("please enter a number for str");
            }
            int strength = Integer.valueOf(strStrength);
            int[] xy = findGridLocation(panels.indexOf(panel));
            System.out.println(xy[0] + ",  " + xy[1]);
            addToStatsTable(name, hp, strength, 1, xy[0], xy[1]);
            displayAllStats();
            troop = new MyTroop(1);
            troops.add(troop);
            panel.add(troop);
        }
        else if (pathFinding ==2){
            if (e.getSource() == panel){
                int panelNum= 0;
                for(int i = 0; i < panels.toArray().length; i++){
                    if (panel == panels.get(i)){
                        panelNum = i;
                    }
                }
                makePlayerRoom(panelNum);
            }
        }
        else if (pathFinding ==3){
            clickCount++;
            if (clickCount <= 2) {
                if (e.getSource() == panel) {
                    if (clickCount == 1) {
                        int[] xy = findGridLocation(panels.indexOf(panel));
                        if (!isATroopThere(xy[0], xy[1])){
                            clickCount = 0;
                        }
                    } else if (clickCount == 2) {
                        startEnd[2] = panels.indexOf(panel);
                    }
                } else {
                    clickCount = 0;
                    resetAllColors();
                }
            }
            if (clickCount == 2) {
                int[] xy = findGridLocation(startEnd[2]);
                moveTroop(1, xy[0], xy[1]);
                resetAllColors();
                clickCount = 0;
            }
            if (clickCount==3){
                resetAllColors();
                clickCount = 0;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        for (int i = 0; i < panels.size(); i++){
            if (e.getSource() == panels.get(i)){
                panel = panels.get(i);
            }
        }

        mouseLocation[0] = panel.getXY()[0] +e.getX();
        mouseLocation[1] = panel.getXY()[1] +e.getY();

        //System.out.println("x : " + mouseLocation[0] + ", y : " + mouseLocation[1]);

        if (clickCount < 2) {
            panel.changeColor(Color.BLUE);
        }

    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (clickCount < 2) {
            panel.resetColor();
        }
    }
}