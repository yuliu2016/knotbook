package kb.service.api.array;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table implementation. Row major. Expandable. Mutable
 * Designed for table UI. Basic processing.
 */
@SuppressWarnings({"unused", "WeakerAccess", "DuplicatedCode"})
public class TableArray {

    public static final byte MODE_NULL = 0;
    public static final byte MODE_INT = 1;
    public static final byte MODE_FLOAT = 2;
    public static final byte MODE_STR = 3;

    public static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("####0.000");

    public int cols;
    public int len;

    // Cell-wise modes
    public ByteArrayList mode = new ByteArrayList();

    // Numbers
    // This uses floats instead of doubles to save memory space
    // for large tables
    public FloatArrayList num = new FloatArrayList();

    // Representation of values
    // Also values itself when the type is a string
    public List<String> str = new ArrayList<>();

    // Headers for this table that can be used to store
    // more information
    public Map<String, String> header = new HashMap<>();

    // The character-width of each column (for pretty-printing)
    public IntArrayList pretty_col_size = new IntArrayList();

    // A formatter used to create float strings
    public DecimalFormat decimalFormat = DEFAULT_FORMAT;

    // Whether the table has headers (for pretty-printing)
    public boolean pretty_headers = false;


    TableArray() {
    }

    void ensureSizeFromRowSize(int rows) {
        len = rows * cols;
        mode.resize(len);
        num.resize(len);
        pretty_col_size.resize(cols);
        for (int i = str.size(); i < len; i++) {
            str.add(null);
        }
    }

    public boolean isPrettyHeaders() {
        return pretty_headers;
    }

    public int getRows() {
        return (len % cols) == 0 ? len / cols : len / cols + 1;
    }

    public String getString(int row, int col) {
        int i = row * cols + col;
        int m = mode.value[i];
        if (m == MODE_NULL) {
            return null;
        } else if (m == MODE_FLOAT) {
            return decimalFormat.format(num.value[i]);
        } else if (m == MODE_INT) {
            return Integer.toString((int) num.value[i]);
        } else {
            return str.get(i);
        }
    }

    public double get(int row, int col) {
        int i = row * cols + col;
        int m = mode.value[i];
        if (m == MODE_FLOAT || m == MODE_INT) {
            return num.value[i];
        } else {
            return Double.NaN;
        }
    }

    public void set(int row, int col, String s) {
        int i = row * cols + col;
        mode.value[i] = MODE_STR;
        str.set(i, s);
    }

    public void set(int row, int col, int s) {
        int i = row * cols + col;
        mode.value[i] = MODE_INT;
        num.value[i] = s;
    }

    public void set(int row, int col, double s) {
        int i = row * cols + col;
        mode.value[i] = MODE_FLOAT;
        num.value[i] = (float) s;
    }

    public boolean isNumber(int row, int col) {
        int i = row * cols + col;
        int m = mode.value[i];
        return m == MODE_INT || m == MODE_FLOAT;
    }

    @Override
    public String toString() {
        return DelimitedHelper.toPrintableString(this);
    }
}