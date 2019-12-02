package kb.service.api.array;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Tables {
    /**
     * Create a new TableArray without anything in it
     */
    public static TableArray emptyArray() {
        return ofSize(0, 0);
    }

    /**
     * Create a new TableArray with a fixed size
     */
    public static TableArray ofSize(int rows, int cols) {
        int len = rows * cols;
        List<String> str = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            str.add(null);
        }
        return new BaseTableArray(cols, len, new byte[len], new float[len], str);
    }

    /**
     * Reads CSV data from an InputStream
     *
     * @param stream  data source
     * @param headers read headers
     * @return the data
     */
    public static TableArray fromCSV(InputStream stream, boolean headers) {
        if (stream == null) {
            throw new NullPointerException("No InputStream");
        }
        return DelimitedHelper.fromCSV(stream, headers);
    }

    /**
     * Saves data to a CSV file
     */
    public static void toCSV(TableArray array, OutputStream stream, char delimiter) {
        DelimitedHelper.toStream(array, stream, delimiter, false);
    }


    /**
     * Reads data from a zip file
     */
    @SuppressWarnings("unused")
    public static TableArray fromZip(File file) {
        return ZipFormatHelper.fromZipFormat(file);
    }

    /**
     * Writes a table to a zip file
     */
    public static void toZip(TableArray array, OutputStream stream) {
        ZipFormatHelper.toZipFormat(array, stream);
    }

    /**
     * Writes data to a HTML string
     */
    public static String toHTML(TableArray array) {
        return HTMLHelper.toHTML(array);
    }

    public static Comparator<Integer> ascendingComparator(TableArray array, int column) {
        return ComparisonHelper.ascendingComparator(array, column);
    }

    public static Comparator<Integer> descendingComparator(TableArray array, int column) {
        return ComparisonHelper.descendingComparator(array, column);
    }

    /**
     * Converts a column index to a string
     */
    public static String columnIndexToString(int col) {
        if (col < 0) {
            throw new IllegalArgumentException("Column cannot be negative");
        }
        if (col < 26) {
            return String.valueOf((char) (65 + col));
        }
        StringBuilder b = new StringBuilder();
        var n = col;
        while (n >= 26) {
            b.insert(0, (char) (65 + n % 26));
            n /= 26;
        }
        b.insert(0, (char) (64 + n));
        return b.toString();
    }

    public static Reference arrayReference(Reference... references) {
        IntArrayList arr = new IntArrayList();
        for (Reference reference : references) {
            for (int i = 0; i < reference.getSize(); i++) {
                arr.appendUnique(reference.getIndex(i));
            }
        }
        return new MultiCellReference(Arrays.copyOf(arr.value, arr.length));
    }

    public static boolean referenceEquals(Reference a, Reference b) {
        IntArrayList ar = new IntArrayList();
        IntArrayList br = new IntArrayList();
        for (int i = 0; i < a.getSize(); i++) {
            ar.append(a.getIndex(i));
        }
        for (int i = 0; i < b.getSize(); i++) {
            br.append(b.getIndex(i));
        }
        int[] aa = ar.copy();
        int[] ba = br.copy();
        Arrays.sort(aa);
        Arrays.sort(ba);
        return Arrays.equals(aa, ba);
    }
}
