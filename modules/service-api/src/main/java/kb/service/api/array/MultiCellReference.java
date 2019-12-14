package kb.service.api.array;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class MultiCellReference implements Reference {

    int[] indices;

    public MultiCellReference(int... indices) {
        this.indices = indices;
        Arrays.sort(indices);
    }

    @Override
    public boolean contains(int index) {
        for (int i : indices) {
            if (i == index) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSize() {
        return indices.length;
    }

    @Override
    public int getIndex(int i) {
        return indices[i];
    }
}
