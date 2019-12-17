package kb.service.api.array;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

public class Tables {
    /**
     * Create a new TableArray without anything in it
     */
    public static TableArray emptyArray() {
        return ofSize(0, 0, false);
    }

    /**
     * Create a new TableArray with a fixed size
     */
    public static TableArray ofSize(int rows, int cols, boolean headers) {
        return ReshapeHelper.ofSize(rows, cols, headers);
    }

    /**
     * Select some columns from a table
     *
     * @param array the array
     * @param columns columns to select
     * @return the selected data
     */
    public static TableArray selectColumns(TableArray array, List<Integer> columns) {
        return ReshapeHelper.selectColumns(array, columns);
    }

    /**
     * Reads CSV data from an InputStream
     *
     * @param stream  data source
     * @param headers read headers
     * @return the data
     */
    public static TableArray fromCSV(InputStream stream, boolean headers) {
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
     * Write a table to a zip file
     */
    public static void toZip(TableArray array, OutputStream stream) {
        ZipFormatHelper.toZipFormat(array, stream);
    }

    /**
     * Write data to a HTML string
     */
    public static String toHTML(TableArray array) {
        return StringHelper.toHTML(array);
    }

    /**
     * Create an ascending comparator
     */
    public static Comparator<Integer> ascendingComparator(TableArray array, int column) {
        return ComparisonHelper.ascendingComparator(array, column);
    }

    /**
     * Create an descending comparator
     */
    public static Comparator<Integer> descendingComparator(TableArray array, int column) {
        return ComparisonHelper.descendingComparator(array, column);
    }

    /**
     * Converts a column index to a string
     */
    public static String columnIndexToString(int col) {
        return StringHelper.columnIndexToString(col);
    }

}
