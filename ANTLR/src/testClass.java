public class testClass {
    private Cell currentCell;

    private class Cell {
        public String fileVar;
        public int[] rowCol = new int[2];

        Cell(String file, int row, int col) {
            fileVar = file;
            rowCol[0] = row;
            rowCol[1] = col;
        }
    }

    public void testObjects() {
        currentCell = new Cell("foo", 1, 1);
        Cell foo = currentCell;
        currentCell = new Cell("bar", 1, 1);
        System.out.println(foo.fileVar);
    }
}
