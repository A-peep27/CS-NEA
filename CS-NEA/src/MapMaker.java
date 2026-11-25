import java.util.Arrays;
import java.util.Random;

public class MapMaker {
    int[][] grid;
    boolean allSpacesFilled = false;
    int x;
    int y;
    int maxX;
    int maxY;
    int count;
    boolean[] free = {false, false, false, false};
    Random random = new Random();
    int avalableCount;

    MapMaker(int hight, int width){
        grid = new int[hight][width];
        x = (int)((width-1)/2) -1;
        y = (int)((hight-1)/2)-1;
        maxX = width -1;
        maxY = hight -1;
        setGrid();
        //showGrid();
    }

    public void setGrid(){
        for (int row = 0; row < maxY; row++) {
            for (int square = 0; square < maxX; square++){
                grid[row][square] = 5;
            }
        }
        grid[(int)(maxY/2)][(int)(maxX/2)] = 4;
    }
    public void checkAvalable(){
        free = new boolean[]{false, false, false, false};
        avalableCount = 0;
        if(grid[y+1][x] == 5){
            free[0] = true;
            avalableCount ++;
        }
        if (grid[y][x+1] == 5){
            free[1] = true;
            avalableCount++;
        }
        if(grid[y-1][x] == 5){
            free[2] = true;
            avalableCount++;
        }
        if(grid[y][x-1] == 5){
            free[3] = true;
            avalableCount++;
        }
    }
    public void showGrid(){
        String[][] stringGrid = new String[maxY][maxX];
        for (int row = 0; row < maxY; row++) {
            for (int square = 0; square < maxX; square++){
                stringGrid[row][square] = String.valueOf(grid[row][square]);
            }
        }
        String formatedGrid = Arrays.deepToString(stringGrid);
        formatedGrid = formatedGrid.replace("],", "\n");
        formatedGrid = formatedGrid.replace("5", " ");
        formatedGrid = formatedGrid.replace("0", " ");
        formatedGrid = formatedGrid.replace(",", "");
        formatedGrid = formatedGrid.replace("[","");
        formatedGrid = formatedGrid.replace("]","");
        System.out.println(formatedGrid);
    }
    public void fillGrid(){
        allSpacesFilled = false;
        int attemptCount = 0;
        count = 1;
        int tempCount = 0;
        setGrid();

        while (!allSpacesFilled && attemptCount <=10*maxX){
            try{
                for(int row = 0; row < maxY; row++) {
                    y = row;
                    for(int column = 0; column <maxX; column++){
                        x = column;
                        if (grid[y][x] != 5) {
                            checkAvalable();
                            tempCount = 0;
                            if (grid[y][x] == 3) {
                                if (avalableCount == 0) {
                                    if (free[0] == false) {
                                    } // this is just to make sure nothing happens
                                } else {
                                    while (tempCount > 1) {
                                        int tempRan = random.nextInt(4);
                                        if (free[tempRan]) {
                                            free[tempRan] = false;
                                            tempCount++;
                                        }
                                    }
                                }
                            } else if (grid[y][x] == 2) {
                                if (avalableCount == 0) {
                                    if (free[0] == false) {
                                    } // this is just to make sure nothing happens
                                } else if (avalableCount >= 2) {
                                    free = new boolean[]{false, false, false, false};
                                } else if (avalableCount == 3 || avalableCount == 4) {
                                    while (tempCount >= 1) {
                                        int tempRan = random.nextInt(4);
                                        if (free[tempRan]) {
                                            free[tempRan] = false;
                                            tempCount++;
                                        }
                                    }
                                }
                            } else if (grid[y][x] == 1) {
                                if (avalableCount == 4) {
                                    while (tempCount >= 2) {
                                        int tempRan = random.nextInt(4);
                                        if (free[tempRan]) {
                                            free[tempRan] = false;
                                            tempCount++;
                                        }
                                    }
                                } else {
                                    free = new boolean[]{false, false, false, false};
                                }
                            } else if (grid[y][x] == 4) {
                                if (free[0] == false) {
                                }
                            } else {
                                free = new boolean[]{false, false, false, false};
                            }

                            int xTemp;
                            int yTemp;

                            yTemp = y - 1;
                            if (free[2] && grid[yTemp][x] == 5) {
                                grid[yTemp][x] = random.nextInt(4) + 1;
                                count++;
                            }

                            yTemp = y + 1;
                            if (free[0] && grid[yTemp][x] == 5) {
                                grid[yTemp][x] = random.nextInt(4) + 1;
                                count++;
                            }

                            xTemp = x - 1;
                            if (free[3] && grid[y][xTemp] == 5) {
                                grid[y][xTemp] = random.nextInt(4) + 1;
                                count++;
                            }

                            xTemp = x + 1;
                            if (free[1] && grid[y][xTemp] == 5) {
                                grid[y][xTemp] = random.nextInt(4) + 1;
                                count++;
                            }

                            if (Arrays.equals(free, new boolean[]{false, false, false, false})) {
                                attemptCount++;
                            } else {
                                attemptCount = 0;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                allSpacesFilled = true;
            }

            //x = random.nextInt(maxX);
            //y = random.nextInt(maxY);

        }
        //System.out.println("map made");
    }

    public int[][] getGrid(){

        for (int row = 0; row < maxY; row++) {
            for (int square = 0; square < maxX; square++){
                if (grid[row][square] != 5){
                    grid[row][square] = 1;
                } else{
                    grid[row][square] = 0;
                }
            }
        }
        return grid;
    }
    public int getCount(){
        return count;
    }
    public void cullGrid(){

        boolean firstSquare = false;
        int rowCount = 0;
        int usedRows = 0;
        int[] unusedRows = new int[maxY];

        for (int row = 0; row < maxY; row++){
            int counter = 0;
            for (int colmeb = 0; colmeb < maxX; colmeb++){
                if (grid[row][colmeb] == 5){
                    counter ++;
                }
            }
            if (counter == maxX){
                unusedRows[row] = 1;
                if(!firstSquare){rowCount++;}
            }
            else{
                unusedRows[row] = 0;
                usedRows++;
                firstSquare = true;
            }
        }

        firstSquare = false;
        int colembCount = 0;
        int usedColembs = 0;
        int[] unusedColembs = new int[maxX];

        for (int colmeb =0 ; colmeb <maxX; colmeb++){
            int counter = 0;
            for (int row = 0; row <maxY; row++){
                if(grid[row][colmeb] == 5){
                    counter++;
                }
            }

            if(counter == maxY){
                unusedColembs[colmeb] = 1;
                if(!firstSquare){colembCount++;}
            }

            else{
                unusedColembs[colmeb] = 0;
                usedColembs++;
                firstSquare = true;
            }
        }

        int[][] newMap = new int[usedRows][usedColembs];
        for(int row = 0; row < maxY; row++){
            if (unusedRows[row] == 0) {
                for (int square = 0; square < maxX; square++) {
                    if(unusedColembs[square] == 0){
                        newMap[row- rowCount][square- colembCount] = grid[row][square];
                    }
                }
            }
        }
        grid = newMap;
        maxX = usedColembs;
        maxY = usedRows;
    }
    public void makeMapFull(){
        for(int row = 0; row<maxY; row++){
            for (int square = 0; square < maxX; square++){
                if(grid[row][square] == 5){
                    grid[row][square] = 8;
                }
            }
        }
    }

}