package kb.service.api.array;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Table implementation.
 */
@SuppressWarnings({"unused", "WeakerAccess", "DuplicatedCode"})
public abstract class TableArray {

    public static final byte MODE_NULL = 0;
    public static final byte MODE_INT = 1;
    public static final byte MODE_FLOAT = 2;
    public static final byte MODE_STR = 3;

    public static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("####0.000");

    public final int cols;
    public final int len;

    // Cell-wise modes
    public final byte[] mode;

    // Numbers
    // This uses floats instead of doubles to save memory space
    // for large tables
    public final float[] num;

    // Representation of values
    // Also values itself when the type is a string
    public final List<String> str;

    // A formatter used to create float strings
    public DecimalFormat decimalFormat = DEFAULT_FORMAT;

    public TableArray(int cols, int len, byte[] mode, float[] num, List<String> str) {
        this.cols = cols;
        this.len = len;
        this.mode = mode;
        this.num = num;
        this.str = str;
    }

    public int getRows() {
        return (len % cols) == 0 ? len / cols : len / cols + 1;
    }

    public String getString(int row, int col) {
        int i = row * cols + col;
        int m = mode[i];
        if (m == MODE_NULL) {
            return null;
        } else if (m == MODE_FLOAT) {
            return decimalFormat.format(num[i]);
        } else if (m == MODE_INT) {
            return Integer.toString((int) num[i]);
        } else {
            return str.get(i);
        }
    }

    public double get(int row, int col) {
        int i = row * cols + col;
        int m = mode[i];
        if (m == MODE_FLOAT || m == MODE_INT) {
            return num[i];
        } else {
            return Double.NaN;
        }
    }

    public void set(int row, int col, String s) {
        int i = row * cols + col;
        mode[i] = MODE_STR;
        str.set(i, s);
    }

    public void set(int row, int col, int s) {
        int i = row * cols + col;
        mode[i] = MODE_INT;
        num[i] = s;
    }

    public void set(int row, int col, double s) {
        int i = row * cols + col;
        mode[i] = MODE_FLOAT;
        num[i] = (float) s;
    }

    public boolean isNumber(int row, int col) {
        int i = row * cols + col;
        int m = mode[i];
        return m == MODE_INT || m == MODE_FLOAT;
    }

    @Override
    public String toString() {
        return DelimitedHelper.toPrintableString(this);
    }
}