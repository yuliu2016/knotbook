package knotbook.core.utils;

@SuppressWarnings("unused")
public class TableHeaders {
    public String columnIndexToString(int col) {
        if (col < 0) {
            return String.valueOf(col);
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
}
