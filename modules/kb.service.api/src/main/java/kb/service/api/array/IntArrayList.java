package kb.service.api.array;

import java.util.Arrays;

@SuppressWarnings({"WeakerAccess", "unused"})
public class IntArrayList {
    public int[] value = new int[0];

    public int length;

    public void resize(int size) {
        if (size != value.length) {
            value = Arrays.copyOf(value, size);
        }
    }

    public void append(int f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        value[length++] = f;
    }

    public void appendUnique(int f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        for (int i : value) {
            if (i == f) {
                return;
            }
        }
        value[length++] = f;
    }

    public void insert(int index, int f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        System.arraycopy(value, index, value, index + 1, length - index);
        value[index] = f;
    }

    public int[] copy() {
        return Arrays.copyOf(value, length);
    }
}
