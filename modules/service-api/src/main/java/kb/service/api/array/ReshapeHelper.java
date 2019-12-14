package kb.service.api.array;

import java.util.ArrayList;
import java.util.List;

class ReshapeHelper {
    public static TableArray ofSize(int rows, int cols, boolean headers) {
        int len = rows * cols;
        List<String> str = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            str.add(null);
        }
        return headers ? new HeaderTableArray(cols, len, new byte[len], new float[len], str) :
                new SimpleTableArray(cols, len, new byte[len], new float[len], str);
    }

    public static TableArray selectColumns(TableArray array, List<Integer> columns) {
        int rows = array.getRows();
        int cols = columns.size();
        TableArray copy = ofSize(rows, cols, array instanceof HeaderTableArray);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int from = i * array.cols + columns.get(j);
                int to = i * cols + j;
                copy.mode[to] = array.mode[from];
                copy.num[to] = array.mode[from];
                copy.str.set(to, array.str.get(from));
            }
        }
        if (array instanceof HeaderTableArray) {
            HeaderTableArray headerArray = (HeaderTableArray) array;
            HeaderTableArray headerCopy = (HeaderTableArray) copy;
            for (int j = 0; j < cols; j++) {
                headerCopy.pretty_col_size[j] = headerArray.pretty_col_size[columns.get(j)];
            }
        }
        return copy;
    }
}
