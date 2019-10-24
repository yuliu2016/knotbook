package kb.service.api.array;

@SuppressWarnings("unused")
public class TableUtil {
    public static String columnIndexToString(int col) {
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


    public static boolean isFormula(String s) {
        return !s.isEmpty() && s.charAt(0) == '=';
    }

    public static Reference arrayReference(Reference[] references) {
        IntArrayList arr = new IntArrayList();
        for (Reference reference : references) {
            for (int i = 0; i < reference.getSize(); i++) {
                arr.appendUnique(reference.getIndex(i));
            }
        }
        return new MultiCellReference(arr.value);
    }
}
