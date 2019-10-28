package kb.service.api.array;

import java.util.Arrays;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ByteArrayList {
    byte[] value = new byte[0];

    int length;

    void resize(int size) {
        if (size != value.length) {
            value = Arrays.copyOf(value, size);
        }
    }

    void append(byte f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        value[length++] = f;
    }

    void appendUnique(byte f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        for (float i : value) {
            if (i == f) {
                return;
            }
        }
        value[length++] = f;
    }

    void insert(int index, byte f) {
        if (length == value.length) {
            resize(value.length + 16);
        }
        System.arraycopy(value, index, value, index + 1, length - index);
        value[index] = f;
    }

    byte[] copy() {
        return Arrays.copyOf(value, length);
    }
}
