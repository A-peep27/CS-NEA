public class SpiralGrid {

    public static int[][] createSpiral(int size) {
        int[][] grid = new int[size][size];

        int row = size / 2;
        int col = size / 2;
        int value = 0;

        // Directions: Right, Down, Left, Up
        int[] dRow = {0, 1, 0, -1};
        int[] dCol = {1, 0, -1, 0};

        grid[row][col] = value++;
        int stepLength = 1;
        int dir = 0;

        while (value < size * size) {
            // Each step length is used twice (e.g., Right 1, Down 1, Left 2, Up 2...)
            for (int repeat = 0; repeat < 2 && value < size * size; repeat++) {
                for (int s = 0; s < stepLength && value < size * size; s++) {
                    row += dRow[dir];
                    col += dCol[dir];

                    if (row >= 0 && row < size && col >= 0 && col < size) {
                        grid[row][col] = value++;
                    }
                }
                dir = (dir + 1) % 4;
            }
            stepLength++;
        }

        return grid;
    }

    public static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int val : row) {
                System.out.printf("%3d ", val);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] spiral = createSpiral(9);
        printGrid(spiral);
    }
}