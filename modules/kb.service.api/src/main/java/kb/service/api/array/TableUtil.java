package kb.service.api.array;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
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
        return new MultiCellReference(arr.value); // todo check size
    }

    public static String formatString(String s, int p) {
        return s + " ".repeat(Math.max(0, p - s.length()));
    }

    public static String formatFloat(float f, int p) {
        String n = Float.toString(f);
        return " ".repeat(Math.max(0, p - n.length())) + n;
    }

    public static String formatInt(float f, int p) {
        String n = Integer.toString((int) f);
        return " ".repeat(Math.max(0, p - n.length())) + n;
    }

    public static int headerWidth(String s) {
        String[] words = s.split(" ");
        int max = 0;
        for (String word : words) {
            if (word.length() > max) {
                max = word.length();
            }
        }
        return max;
    }

    public static String[] split(String s) {
        List<String> sp = new ArrayList<>();
        int i = 0;
        boolean quotes = false;
        while (i < s.length()) {
            int j = i;
            while (j < s.length()) {
                char ch = s.charAt(j);
                if (ch == '\"') {
                    quotes = !quotes;
                }
                if (ch == ',' && !quotes) {
                    break;
                }
                j++;
            }

            String sub = s.substring(i, j);
            if (sub.startsWith("\"")) {
                sub = sub.substring(1);
            }
            if (sub.endsWith("\"")) {
                sub = sub.substring(0, sub.length() - 1);
            }
            sp.add(sub);
            i = j + 1;
        }
        return sp.toArray(new String[0]);
    }
}
