package kb.service.api.array;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Efficient table array implementation, where operations can
 * mostly be vectorized
 */
@SuppressWarnings("unused")
public class TableArray {

    private int cols;
    private int rows;
    private int len;

    // Cell-wise types
    private byte[] types;

    // Values
    // This uses floats instead of doubles to save memory space
    // for large tables
    private float[] values;

    // Representation of values
    // Also values itself when the type is a string
    private String[] str;

    // No Value
    private static final byte TYPE_NA = (byte) 0b00000000;

    // Char Sequence
    private static final byte TYPE_CHAR_SEQUENCE = (byte) 0b00000001;

    // Floating Point
    private static final byte TYPE_FLOAT = (byte) 0b00000010;

    private TableArray(int rows, int cols, byte[] types, float[] values, String[] str) {

        this.rows = rows;
        this.cols = cols;
        this.types = types;
        this.values = values;
        this.str = str;

        if (types == null || values == null || str == null) {
            throw new NullPointerException();
        }

        len = types.length;

        if (!(len == values.length && len == str.length && rows * cols == len)) {
            throw new IllegalStateException();
        }
    }


    private int ix(int row, int col) {
        if (row < 0 || row > rows || col < 0 || col > cols) {
            throw new IndexOutOfBoundsException();
        }
        return col * rows + row;
    }

    public int getSize() {
        return len;
    }

    public void reshape(int rows, int cols) {
        if (!(rows * cols == len)) {
            throw new IllegalArgumentException();
        }

        this.rows = rows;
        this.cols = cols;
    }

    public boolean isNA(int row, int col) {
        return types[ix(row, col)] == TYPE_NA;
    }

    public boolean isString(int row, int col) {
        return types[ix(row, col)] == TYPE_CHAR_SEQUENCE;
    }

    public boolean isNumber(int row, int col) {
        return types[ix(row, col)] == TYPE_FLOAT;
    }

    public float[] getValuesCopy() {
        return Arrays.copyOf(values, len);
    }

    public String[] getStringsCopy() {
        return Arrays.copyOf(str, len);
    }

    public Iterator<String> stringIterator() {
        return Arrays.stream(str).iterator();
    }

    public double getAverage() {
        double sum = 0.0;
        double count = 0.0;
        for (int i = 0; i < len; i++) {
            if (types[i] == TYPE_FLOAT) {
                sum += values[i];
                count++;
            }
        }
        return sum / count;
    }
}
