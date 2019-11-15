package kb.service.api.array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public static boolean isFormula(String s) {
        return !s.isEmpty() && s.charAt(0) == '=';
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

    public static int widthForSplitHeader(String s) {
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

    public static List<String[]> fromCSV(InputStream stream) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            int cols = -1;
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                String[] sp = split(line);
                if (cols == -1) {
                    cols = sp.length;
                } else if (sp.length > cols) {
                    throw new IllegalStateException("More value than headers");
                }
                data.add(sp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String toHTML(TableArray array) {
        StringBuilder html = new StringBuilder("<table style='width:100%'><tr>");
        int start = 0;
        if (array.isPrettyHeaders()) {
            start = 1;
            for (int i = 0; i < array.getCols(); i++) {
                html.append("<th>").append(array.get(0, i)).append("</th>");
            }
        }
        html.append("</tr>");
        for (int i = start; i < array.getRows(); i++) {
            html.append("<tr>");
            for (int j = 0; j < array.getCols(); j++) {
                html.append("<td>").append(array.get(i, j)).append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }
}
