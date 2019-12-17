package kb.service.api.array;

import java.util.List;

final class HeaderTableArray extends TableArray{

    // The character-width of each column (for pretty-printing)
    public int[] pretty_col_size;

    public HeaderTableArray(int cols, int len, byte[] mode, float[] num, List<String> str) {
        super(cols, len, mode, num, str);
        pretty_col_size = new int[cols];
    }
}
