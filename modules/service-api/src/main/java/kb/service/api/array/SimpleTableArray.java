package kb.service.api.array;

import java.util.List;

final class SimpleTableArray extends TableArray {
    public SimpleTableArray(int cols, int len, byte[] mode, float[] num, List<String> str) {
        super(cols, len, mode, num, str);
    }
}
