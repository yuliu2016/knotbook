package kb.service.api.array;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Table implementation. Row major. Expandable.
 * UI central. Basic processing. Formulas
 */
@SuppressWarnings({"unused", "WeakerAccess", "DuplicatedCode"})
public class TableArray {

    private int cols;
    private int len;

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
    private List<Reference[]> ref = new ArrayList<>();

    // The last formula provider
    private FormulaProvider last_provider = null;

    // The character-width of each column (for pretty-printing)
    private IntArrayList pretty_col_size = new IntArrayList();

    // Whether the table has headers (for pretty-printing)
    private boolean pretty_headers = false;

    private static final byte MODE_NULL = 0;
    private static final byte MODE_INT = 1;
    private static final byte MODE_FLOAT = 2;
    private static final byte MODE_STR = 3;

    public static TableArray emptyTableArray() {
        return new TableArray();
    }

    /**
     * Reads CSV data from an InputStream
     *
     * @param stream  data source
     * @param headers read headers
     * @return the data
     */
    public static TableArray fromCSV(InputStream stream, boolean headers) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            List<String[]> data = new ArrayList<>();
            int cols = -1;
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                String[] sp = TableUtil.split(line);
                if (cols == -1) {
                    cols = sp.length;
                } else if (sp.length > cols) {
                    throw new IllegalStateException("More value than headers");
                }
                data.add(sp);
            }
            TableArray array = emptyTableArray();
            if (data.isEmpty()) {
                return array;
            }
            array.cols = cols;
            array.ensureSizeFromDimension(data.size());

            int startIndex = 0;
            if (headers) {
                // title row
                String[] titles = data.get(0);
                for (int i = 0; i < cols; i++) {
                    array.mode.value[i] = MODE_STR;
                    array.pretty_col_size.value[i] = TableUtil.headerWidth(titles[i]);
                    array.str.add(titles[i]);
                }
                array.pretty_headers = true;
                startIndex = 1;
            }
            for (int i = startIndex; i < data.size(); i++) {
                String[] row = data.get(i);
                for (int j = 0; j < array.cols; j++) {
                    int ai = i * array.cols + j;
                    if (j >= row.length) {
                        array.mode.value[ai] = 0;
                        array.str.add(null);
                        continue;
                    }
                    String v = row[j].strip();
                    if (v.isEmpty()) {
                        array.mode.value[ai] = 0;
                        array.str.add(null);
                    } else {
                        try {
                            float fv = Float.parseFloat(v);
                            array.num.value[ai] = fv;
                            // check for integer state
                            String f;
                            if (fv % 1 == 0 && fv >= 0 && fv < 65536) {
                                array.mode.value[ai] = MODE_INT;
                                f = Integer.toString((int) fv);
                            } else {
                                array.mode.value[ai] = MODE_FLOAT;
                                f = Float.toString(fv);
                            }
                            array.pretty_col_size.value[j] = Math
                                    .max(array.pretty_col_size.value[j], f.length());
                            array.str.add(f);
                        } catch (NumberFormatException e) {
                            array.mode.value[ai] = MODE_STR;
                            array.pretty_col_size.value[j] = Math
                                    .max(array.pretty_col_size.value[j], v.length());
                            array.str.add(v);
                        }
                    }
                }
            }
            return array;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyTableArray();
        }
    }

    /**
     * Extracts data from a file
     *
     * @param file The file path. Must be a real file. If an InputStream is used
     *             it must be saved to a temporary file before using this
     * @return the data
     */
    public static TableArray fromKBT(File file) {
        try (ZipFile zip = new ZipFile(file)) {
            TableArray array = emptyTableArray();
            ZipEntry modeEntry = zip.getEntry("array/mode");
            InputStream modeIn = zip.getInputStream(modeEntry);
            array.mode.value = modeIn.readAllBytes();
            array.mode.length = array.mode.value.length;
            array.len = array.mode.length;
            ZipEntry numEntry = zip.getEntry("array/num");
            DataInputStream numIn = new DataInputStream(zip.getInputStream(numEntry));
            array.num.resize(array.len);
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                if (m == MODE_INT || m == MODE_FLOAT) {
                    array.num.value[i] = numIn.readFloat();
                }
            }
            ZipEntry strEntry = zip.getEntry("array/str");
            BufferedReader strIn = new BufferedReader(new InputStreamReader(zip.getInputStream(numEntry)));
            for (int i = 0; i < array.len; i++) {
                int m = array.mode.value[i];
                array.str.add(m == MODE_STR ? strIn.readLine() : null);
            }
            return array;
        } catch (IOException e) {
            e.printStackTrace();
            return emptyTableArray();
        }
    }

    private TableArray() {
    }

    private void ensureSizeFromDimension(int rows) {
        len = rows * cols;
        mode.resize(len);
        num.resize(len);
        pretty_col_size.resize(len);
    }


    public int getCols() {
        return cols;
    }

    public int getSize() {
        return len;
    }

    public int getRows() {
        return (len % cols) == 0 ? len / cols : len / cols + 1;
    }

    public String get(int row, int col) {
        int i = row * cols + col;
        int m = mode.value[i];
        if (m == MODE_NULL) {
            return null;
        } else if (m == MODE_FLOAT) {
            return Float.toString(num.value[i]);
        } else if (m == MODE_INT) {
            return Integer.toString((int) num.value[i]);
        } else {
            return str.get(i);
        }
    }

    public boolean isNumber(int row, int col) {
        int i = row * cols + col;
        int m = mode.value[i];
        return m == MODE_INT || m == MODE_FLOAT;
    }

    public double getAverage() {
        double sum = 0.0;
        double count = 0.0;
        for (int i = 0; i < len; i++) {
            if (mode.value[i] == MODE_INT || mode.value[i] == MODE_FLOAT) {
                sum += num.value[i];
                count++;
            }
        }
        return sum / count;
    }

    public void delimitToStream(OutputStream stream,
                                String delimiter,
                                boolean prettyPrint) {
        boolean isNum = false;
        int startRow;
        if (pretty_headers) {
            startRow = 1;
        } else {
            startRow = 0;
        }
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(stream))) {
            for (int i = startRow; i < (len / cols); i++) {
                if (prettyPrint) {
                    if (pretty_headers && (i - startRow) % 100 == 0) {
                        w.write(getPrettyHeaders());
                    }
                    w.write("\033[37m" + TableUtil.formatInt(i, 4) + "\033[0m\t");
                }
                for (int j = 0; j < cols; j++) {
                    int ai = i * cols + j;
                    int m = mode.value[ai];
                    int padding = pretty_col_size.value[j];
                    if (m != MODE_NULL) {
                        if (m == MODE_INT || m == MODE_FLOAT) {
                            float f = num.value[ai];
                            if (prettyPrint) {
                                isNum = true;
                                w.write("\033[34m");
                            }
                            if (m == MODE_FLOAT) {
                                w.write(prettyPrint ? TableUtil
                                        .formatFloat(f, padding) : Float.toString(f));
                            } else {
                                w.write(prettyPrint ? TableUtil
                                        .formatInt(f, padding) : Integer.toString((int) f));
                            }
                        } else {
                            String s = str.get(ai);
                            if (prettyPrint) {
                                if (isNum) {
                                    isNum = false;
                                    w.write("\033[0m");
                                }
                                w.write(TableUtil.formatString(s, padding));
                            } else {
                                w.write(s);
                            }
                        }
                    } else if (prettyPrint) {
                        if (isNum) {
                            isNum = false;
                            w.write("\033[0m");
                        }
                        w.write(" ".repeat(padding));
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

    public String getPrettyHeaders() {
        StringBuilder b = new StringBuilder();
        b.append("\033[0m");
        b.append("    \t");
        for (int i = 0; i < cols; i++) {
            int colSize = pretty_col_size.value[i];
            b.append("=".repeat(colSize));
            b.append("\t");
        }
        b.append("\n");
        b.append("\033[35m");
        int maxHeight = 1;
        List<String[]> headers = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            String[] sp = str.get(i).split(" ");
            maxHeight = Math.max(maxHeight, sp.length);
            headers.add(sp);
        }
        for (int i = 0; i < maxHeight; i++) {
            b.append("    \t");
            for (int j = 0; j < headers.size(); j++) {
                int w = pretty_col_size.value[j];
                String[] sp = headers.get(j);
                int e = i - (maxHeight - sp.length);
                if (e < 0) {
                    b.append(" ".repeat(w));
                } else {
                    b.append(TableUtil.formatString(sp[e], w));
                }
                b.append("\t");
            }
            b.append("\n");
        }
        b.append("\033[0m");
        b.append("    \t");
        for (int i = 0; i < cols; i++) {
            int colSize = pretty_col_size.value[i];
            b.append("=".repeat(colSize));
            b.append("\t");
        }
        b.append("\n");
        return b.toString();
    }

    public void toKBT(OutputStream stream) {
        try (ZipOutputStream out = new ZipOutputStream(stream)) {
            out.setMethod(ZipEntry.DEFLATED);
            out.setLevel(9);
            ZipEntry modeEntry = new ZipEntry("array/mode");
            out.putNextEntry(modeEntry);
            out.write(mode.value, 0, len);
            out.closeEntry();
            ZipEntry numEntry = new ZipEntry("array/num");
            out.putNextEntry(numEntry);
            DataOutputStream numStream = new DataOutputStream(out);
            for (int i = 0; i < len; i++) {
                int m = mode.value[i];
                float v = num.value[i];
                if (m == MODE_INT || m == MODE_FLOAT) {
                    if (m == MODE_FLOAT) {
                        numStream.writeFloat(v);
                    } else {
                        numStream.writeShort((int) v);
                    }
                }
            }
            numStream.flush();
            out.closeEntry();
            ZipEntry strEntry = new ZipEntry("array/str.txt");
            out.putNextEntry(strEntry);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out), 64);
            for (int i = 0; i < len; i++) {
                int m = mode.value[i];
                if (m == MODE_STR) {
                    String s = str.get(i);
                    writer.write(s);
                    writer.newLine();
                }
            }
            writer.flush();
            out.closeEntry();
            header.put("columns", String.valueOf(cols));
            ZipEntry headerEntry = new ZipEntry("array/header.txt");
            out.putNextEntry(headerEntry);
            BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out), 64);
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headerWriter.write(entry.getKey());
                headerWriter.write('=');
                headerWriter.write(entry.getValue());
            }
            headerWriter.flush();
            out.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void applyFormulas(FormulaProvider provider) {
        if (provider != last_provider) {
            last_provider = provider;
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
            if (av == MODE_NULL) {
                return bv == MODE_NULL ? 0 : 1;
            } else {
                if (bv == MODE_NULL) {
                    return -1;
                } else {
                    if (av == MODE_INT || av == MODE_FLOAT) {
                        return bv == MODE_INT || bv == MODE_FLOAT ? Float
                                .compare(num.value[ai], num.value[bi]) : 1;
                    } else {
                        return bv == MODE_INT || bv == MODE_FLOAT ? -1 : NaturalOrderComparator
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
            if (av == MODE_NULL) {
                return bv == MODE_NULL ? 0 : 1;
            } else {
                if (bv == MODE_NULL) {
                    return -1;
                } else {
                    if (av == MODE_INT || av == MODE_FLOAT) {
                        return bv == MODE_INT || bv == MODE_FLOAT ? Float
                                .compare(num.value[bi], num.value[ai]) : -1;
                    } else {
                        return bv == MODE_INT || bv == MODE_FLOAT ? 1 : NaturalOrderComparator
                                .compareNaturally(str.get(bi), str.get(ai));
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