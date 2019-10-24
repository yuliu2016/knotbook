package kb.service.api.array;

import java.io.*;
import java.util.*;

/**
 * Table implementation. Row major. Expandable.
 * UI central. Basic processing. Formulas
 */
@SuppressWarnings({"unused", "WeakerAccess", "DuplicatedCode"})
public class TableArray {

    private int cols;
    private int rows;
    private int len;


    // bit 0 - 1: exists   0: NA !!
    private static final byte MODE_EXISTS_NA = 1;

    // bit 1 - 1: str      0: number !!
    private static final byte MODE_STR_NUM = 2;

    // bit 2 - 1: int      0: float !!
    private static final byte MODE_INT_FLOAT = 4;

    // bit 3 - 1: formula  0: none ::
    private static final byte MODE_FORMULA = 8;

    // bit 4 - 1: editable 0: locked !!
    private static final byte MODE_UNLOCKED = 16;

    // bit 5 - 1: resolved 0: unknown ::
    private static final byte MODE_RESOLVED = 32;

    // bit 6 - 1: bold     0: normal !!
    private static final byte MODE_BOLD = 64;

    // bit 7 - 1: valid    0: invalid ::
    private static final byte MODE_VALID = -128;


    // Cell-wise modes
    private ByteArrayList mode = new ByteArrayList();

    // Numbers
    // This uses floats instead of doubles to save memory space
    // for large tables
    private FloatArrayList num = new FloatArrayList();

    // Representation of values
    // Also values itself when the type is a string
    private List<String> str = new ArrayList<>();

    // Headers for this table that can be used to store
    // more information
    private Map<String, String> header = new HashMap<>();

    // Used for caching references returned by the formula
    // provider
    private Map<Integer, Reference[]> ref = new HashMap<>();

    // The number of invalid cells
    private int invalidity;

    // The last formula provider
    private FormulaProvider lastProvider;

    // The character-width of each column
    private IntArrayList columnWidths = new IntArrayList();


    public static TableArray emptyTableArray() {
        return new TableArray();
    }

    public static TableArray load(InputStream in) {
        return emptyTableArray();
    }


    public static TableArray zeros(int r, int c) {
        return new TableArray();
    }

    public static TableArray ones(int r, int c) {
        return new TableArray();
    }

    public static TableArray full(int r, int c, float v) {
        return new TableArray();
    }

    public static TableArray full(int r, int c, String v) {
        return new TableArray();
    }

    public static TableArray full(int r, int c, int v) {
        return new TableArray();
    }

    public static TableArray fromCSV(InputStream stream, String delimiter) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            List<String[]> data = new ArrayList<>();
            int cols = -1;
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                String[] sp = line.split(delimiter);
                if (cols == -1) {
                    cols = sp.length;
                } else if (sp.length > cols) {
                    throw new IllegalStateException("More value headers than ");
                }
                data.add(sp);
            }
            // no data
            if (data.isEmpty()) {
                return emptyTableArray();
            }
            TableArray array = new TableArray();
            array.cols = cols;
            array.rows = data.size();
            array.ensureSizeFromDimension();
            // title row
            array.str.addAll(Arrays.asList(data.get(0)));
            for (int i = 0; i < cols; i++) {
                array.mode.value[i] = MODE_EXISTS_NA | MODE_STR_NUM | MODE_BOLD;
            }
            // only headers; no data
            if (data.size() == 1) {
//                array.validate();
                return array;
            }
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                for (int j = 0; j < array.cols; j++) {
                    int ai = i * array.cols + j;
                    if (j > row.length) {
                        array.mode.value[ai] = 0;
                        array.str.add(null);
                    }
                    String v = row[j].strip();
                    if (v.isEmpty()) {
                        array.mode.value[ai] = 0;
                        array.str.add(null);
                    } else {
                        try {
                            float fv = Float.parseFloat(v);
                            array.num.value[ai] = fv;
                            if (fv % 1 == 0) { // check for integer
                                array.mode.value[ai] = MODE_EXISTS_NA | MODE_INT_FLOAT;
                                array.str.add(Integer.toString((int) fv));
                            } else {
                                array.mode.value[ai] = MODE_EXISTS_NA;
                                array.str.add(Float.toString(fv));
                            }
                        } catch (NumberFormatException e) {
                            array.mode.value[ai] = MODE_EXISTS_NA | MODE_STR_NUM;
                            array.str.add(v);
                        }
                    }
                }
            }
//            array.validate();
            return array;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyTableArray();
        }
    }

    private TableArray() {
    }

    private void ensureSizeFromDimension() {
        len = rows * cols;
        mode.resize(len);
        num.resize(len);
    }

    private int ix(int row, int col) {
        if (row < 0 || row > rows || col < 0 || col > cols) {
            throw new IndexOutOfBoundsException();
        }
        return row * cols + col;
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
        return (mode.value[ix(row, col)] & MODE_EXISTS_NA) == 0;
    }

    public boolean isString(int row, int col) {
        return (mode.value[ix(row, col)] & MODE_STR_NUM) != 0;
    }

    public boolean isNumber(int row, int col) {
        return (mode.value[ix(row, col)] & MODE_STR_NUM) == 0;
    }

    public Iterator<String> stringIterator() {
        return str.iterator();
    }

    public double getAverage() {
        double sum = 0.0;
        double count = 0.0;
        for (int i = 0; i < len; i++) {
            if ((mode.value[i] & MODE_STR_NUM) == 0 && (mode.value[i] & MODE_EXISTS_NA) == 1) {
                sum += num.value[i];
                count++;
            }
        }
        return sum / count;
    }

    public void put(int row, int col, double number) {
        int i = ix(row, col);
        mode.value[i] = MODE_EXISTS_NA;
        invalidity++;
        num.value[i] = (float) number;
    }

    public void delimitToStream(OutputStream stream, String delimiter, boolean prettyPrint) {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(stream))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int ai = i * cols + j;
                    int m = mode.value[ai];
                    if ((m & MODE_EXISTS_NA) != 0) {
                        if ((m & MODE_STR_NUM) == 0) {
                            if ((m & MODE_INT_FLOAT) == 0) {
                                w.write(Float.toString(num.value[ai]));
                            } else {
                                w.write(Integer.toString((int) num.value[ai]));
                            }
                        } else {
                            w.write(str.get(i * cols + j));
                        }
                    }
                    w.write(delimiter);
                }
                w.newLine();
            }
            w.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void applyFormulas(FormulaProvider provider) {
        if (ref == null) {
            ref = new HashMap<>();
        }
    }

    public Comparator<Integer> ascendingComparator(int col) {
        if (col < 0 || col > cols) {
            throw new IllegalArgumentException();
        }
        return (a, b) -> {
            int ai = a * cols + col;
            int bi = b * cols + col;
            int av = mode.value[ai];
            int bv = mode.value[bi];
            if ((av & MODE_EXISTS_NA) == 0) {
                return (bv & MODE_EXISTS_NA) == 0 ? 0 : 1;
            } else {
                if ((bv & MODE_EXISTS_NA) == 0) {
                    return -1;
                } else {
                    if ((av & MODE_STR_NUM) == 0) {
                        return (bv & MODE_STR_NUM) == 0 ? Float.compare(num.value[ai], num.value[bi]) : 1;
                    } else {
                        return (bv & MODE_STR_NUM) == 0 ? -1 : NaturalOrderComparator
                                .compareNaturally(str.get(ai), str.get(bi));
                    }
                }
            }
        };
    }

    public Comparator<Integer> descendingComparator(int col) {
        if (col < 0 || col > cols) {
            throw new IllegalArgumentException();
        }
        return (a, b) -> {
            int ai = a * cols + col;
            int bi = b * cols + col;
            int av = mode.value[ai];
            int bv = mode.value[bi];
            if ((av & MODE_EXISTS_NA) == 0) {
                return (bv & MODE_EXISTS_NA) == 0 ? 0 : 1;
            } else {
                if ((bv & MODE_EXISTS_NA) == 0) {
                    return -1;
                } else {
                    if ((av & MODE_STR_NUM) == 0) {
                        return (bv & MODE_STR_NUM) == 0 ? Float.compare(num.value[ai], num.value[bi]) : 1;
                    } else {
                        return (bv & MODE_STR_NUM) == 0 ? -1 : NaturalOrderComparator
                                .compareNaturally(str.get(ai), str.get(bi));
                    }
                }
            }
        };
    }

    @Override
    public String toString() {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        delimitToStream(o, "\t", true);
        return o.toString();
    }

    public void debug() {
        System.out.println(str);
        System.out.println(Arrays.toString(num.value));
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < mode.length; i++) {
            b.append(i);
            b.append("\t");
            b.append(String.format("%8s", Integer.toBinaryString(mode.value[i])));
            b.append("\n");
        }
        System.out.println(b);
    }
}