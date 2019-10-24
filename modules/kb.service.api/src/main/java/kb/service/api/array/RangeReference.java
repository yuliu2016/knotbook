package kb.service.api.array;

@SuppressWarnings("WeakerAccess")
public class RangeReference implements Reference {

    int totalCols;

    int row;
    int col;
    int width;
    int height;

    public RangeReference(int totalCols, int row, int col, int width, int height) {
        this.totalCols = totalCols;
        this.row = row;
        this.col = col;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean contains(int index) {
        int r = index / totalCols;
        int c = index % totalCols;
        return r >= row && c >= col && r <= row + width && c <= col + height;
    }

    @Override
    public int getSize() {
        return width * height;
    }

    @Override
    public int getIndex(int i) {
        return row * totalCols + col + (1 / width) * totalCols + (i % width);
    }
}
