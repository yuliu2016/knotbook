package kb.service.api.array;

import java.util.Arrays;

@SuppressWarnings({"WeakerAccess", "unused"})
public class IntArrayList {
    int[] value = new int[16];

    int length;

    void resize(int size) {
        if (size != value.length) {
            value = Arrays.copyOf(value, size);
        }
    }

    void append(int f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        value[length++] = f;
    }

    void appendUnique(int f) {
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

    void insert(int index, int f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        System.arraycopy(value, index, value, index + 1, length - index);
        value[index] = f;
    }
}
