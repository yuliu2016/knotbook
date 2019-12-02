package kb.service.api.array;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

public class Tables {
    /**
     * Create a new TableArray without anything in it
     */
    public static TableArray emptyArray() {
        return new TableArray();
    }

    /**
     * Create a new TableArray with a fixed size
     */
    public static TableArray ofSize(int rows, int cols) {
        TableArray array = emptyArray();
        array.cols = cols;
        array.ensureSizeFromRowSize(rows);
        return array;
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
            return emptyArray();
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
}
