package kb.service.api.array;

class StringHelper {
    public static String toHTML(TableArray array) {
        if (array == null) {
            return "";
        }
        StringBuilder html = new StringBuilder("<table style='width:100%'><tr>");
        int start = 0;
        if (array instanceof HeaderTableArray) {
            start = 1;
            for (int i = 0; i < array.cols; i++) {
                html.append("<th>").append(array.getString(0, i)).append("</th>");
            }
            html.append("\n");
        }
        html.append("</tr>");
        for (int i = start; i < array.getRows(); i++) {
            html.append("<tr>");
            for (int j = 0; j < array.cols; j++) {
                html.append("<td>").append(array.getString(i, j)).append("</td>");
            }
            html.append("</tr>\n");
        }
        html.append("</table>");
        return html.toString();
    }

    public static String columnIndexToString(int col) {
        if (col < 0) {
            throw new IllegalArgumentException("Column cannot be negative");
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
}
