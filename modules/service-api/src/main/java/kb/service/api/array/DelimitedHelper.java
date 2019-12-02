package kb.service.api.array;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static kb.service.api.array.TableArray.*;

class DelimitedHelper {

    public static String[] splitLine(String s) {
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

    public static List<String[]> fromCSVStream(InputStream stream) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(stream))) {
            int cols = -1;
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                String[] sp = splitLine(line);
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

    static TableArray fromCSV(InputStream stream, boolean headers) {
        List<String[]> data = fromCSVStream(stream);
        TableArray array = Tables.emptyArray();
        if (data.isEmpty()) {
            return array;
        }
        int cols = data.get(0).length;
        array.cols = cols;
        array.ensureSizeFromRowSize(data.size());

        int startIndex = 0;

        if (headers) {
            String[] titles = data.get(0);
            for (int i = 0; i < cols; i++) {
                array.mode.value[i] = MODE_STR;
                array.pretty_col_size.value[i] = widthForSplitHeader(titles[i]);
                array.str.set(i, titles[i]);
            }
            array.pretty_headers = true;
            startIndex = 1;
        }

        for (int i = startIndex; i < data.size(); i++) {
            String[] row = data.get(i);
            for (int j = 0; j < array.cols; j++) {
                int ai = i * array.cols + j;
                if (j >= row.length) {
                    array.mode.value[ai] = 0;
                    continue;
                }
                String v = row[j].strip();
                if (v.isEmpty()) {
                    array.mode.value[ai] = 0;
                } else {
                    try {
                        float fv = Float.parseFloat(v);
                        array.num.value[ai] = fv;
                        // check for integer state
                        String f;
                        if (fv % 1 == 0 && fv >= 0 && fv < 65536) {
                            array.mode.value[ai] = MODE_INT;
                            f = Integer.toString((int) fv);
                        } else {
                            array.mode.value[ai] = MODE_FLOAT;
                            f = Float.toString(fv);
                        }
                        array.pretty_col_size.value[j] = Math
                                .max(array.pretty_col_size.value[j], f.length());
                    } catch (NumberFormatException e) {
                        array.mode.value[ai] = MODE_STR;
                        array.pretty_col_size.value[j] = Math
                                .max(array.pretty_col_size.value[j], v.length());
                        array.str.set(ai, v);
                    }
                }
            }
        }
        return array;
    }

    public static void toStream(TableArray array,
                                OutputStream stream,
                                char delimiter,
                                boolean prettyPrint) {
        assert array != null;
        boolean isNum = false;
        int startRow;
        if (array.pretty_headers) {
            startRow = 1;
        } else {
            startRow = 0;
        }
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(stream))) {
            for (int i = startRow; i < (array.len / array.cols); i++) {
                if (prettyPrint) {
                    if (array.pretty_headers && (i - startRow) % 100 == 0) {
                        w.write(getPrettyHeaders(array));
                    }
                    w.write("\033[37m" + formatInt(i, 4) + "\033[0m\t");
                }
                for (int j = 0; j < array.cols; j++) {
                    int ai = i * array.cols + j;
                    int m = array.mode.value[ai];
                    int padding = array.pretty_col_size.value[j];
                    if (m != MODE_NULL) {
                        if (m == MODE_INT || m == MODE_FLOAT) {
                            float f = array.num.value[ai];
                            if (prettyPrint) {
                                isNum = true;
                                w.write("\033[34m");
                            }
                            if (m == MODE_FLOAT) {
                                w.write(prettyPrint ? formatRight(floatFormat.format(f), padding) : floatFormat.format(f));
                            } else {
                                w.write(prettyPrint ? formatInt(f, padding) : Integer.toString((int) f));
                            }
                        } else {
                            String s = array.str.get(ai);
                            if (prettyPrint) {
                                if (isNum) {
                                    isNum = false;
                                    w.write("\033[0m");
                                }
                                w.write(formatString(s, padding));
                            } else {
                                w.write(s);
                            }
                        }
                    } else if (prettyPrint) {
                        if (isNum) {
                            isNum = false;
                            w.write("\033[0m");
                        }
                        w.write(" ".repeat(padding));
                    }
                    w.write(delimiter);
                }
                w.newLine();
            }
            w.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getPrettyHeaders(TableArray array) {
        StringBuilder b = new StringBuilder();
        b.append("\033[0m");
        b.append("    \t");
        for (int i = 0; i < array.cols; i++) {
            int colSize = array.pretty_col_size.value[i];
            b.append("=".repeat(colSize));
            b.append("\t");
        }
        b.append("\n");
        b.append("\033[35m");
        int maxHeight = 1;
        List<String[]> headers = new ArrayList<>();
        for (int i = 0; i < array.cols; i++) {
            String[] sp = array.str.get(i).split(" ");
            maxHeight = Math.max(maxHeight, sp.length);
            headers.add(sp);
        }
        for (int i = 0; i < maxHeight; i++) {
            b.append("    \t");
            for (int j = 0; j < headers.size(); j++) {
                int w = array.pretty_col_size.value[j];
                String[] sp = headers.get(j);
                int e = i - (maxHeight - sp.length);
                if (e < 0) {
                    b.append(" ".repeat(w));
                } else {
                    b.append(formatString(sp[e], w));
                }
                b.append("\t");
            }
            b.append("\n");
        }
        b.append("\033[0m");
        b.append("    \t");
        for (int i = 0; i < array.cols; i++) {
            int colSize = array.pretty_col_size.value[i];
            b.append("=".repeat(colSize));
            b.append("\t");
        }
        b.append("\n");
        return b.toString();
    }

    public static String formatString(String s, int p) {
        return s + " ".repeat(Math.max(0, p - s.length()));
    }

    public static String formatRight(String n, int p) {
        return " ".repeat(Math.max(0, p - n.length())) + n;
    }

    public static String formatInt(float f, int p) {
        return formatRight(Integer.toString((int) f), p);
    }

    @SuppressWarnings("unused")
    public static String formatFloat(float f, int p) {
        return formatRight(Float.toString(f), p);
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
}