package kb.service.api.array;

import java.util.Arrays;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TableUtil {
    public static String columnIndexToString(int col) {
        if (col < 0) {
            throw new IllegalArgumentException();
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

    public static Reference arrayReference(Reference... references) {
        IntArrayList arr = new IntArrayList();
        for (Reference reference : references) {
            for (int i = 0; i < reference.getSize(); i++) {
                arr.appendUnique(reference.getIndex(i));
            }
        }
        return new MultiCellReference(Arrays.copyOf(arr.value, arr.length));
    }

    public static boolean referenceEquals(Reference a, Reference b) {
        IntArrayList ar = new IntArrayList();
        IntArrayList br = new IntArrayList();
        for (int i = 0; i < a.getSize(); i++) {
            ar.append(a.getIndex(i));
        }
        for (int i = 0; i < b.getSize(); i++) {
            br.append(b.getIndex(i));
        }
        int[] aa = ar.copy();
        int[] ba = br.copy();
        Arrays.sort(aa);
        Arrays.sort(ba);
        return Arrays.equals(aa, ba);
    }

}
