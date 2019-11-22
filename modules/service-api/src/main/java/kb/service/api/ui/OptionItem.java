package kb.service.api.ui;

import javafx.scene.Node;

import java.util.Arrays;

@SuppressWarnings("DuplicatedCode")
public class OptionItem {
    private String name;
    private String info1;
    private String info2;
    private Node graphic;
    private boolean[] highlight;

    /**
     * Creates a new item
     *
     * @param name      the name of the item
     * @param info1      additional info
     * @param graphic   a graphic item, like an icon or a checkbox
     * @param highlight the characters to be highlighted
     */
    public OptionItem(String name, String info1, String info2, Node graphic, boolean[] highlight) {
        this.name = name;
        this.info1 = info1;
        this.info2 = info2;
        this.graphic = graphic;
        this.highlight = highlight;
    }

    public String getName() {
        return name;
    }

    public String getInfo1() {
        return info1;
    }

    public String getInfo2() {
        return info2;
    }

    public Node getGraphic() {
        return graphic;
    }

    public boolean[] getHighlight() {
        return highlight;
    }

    public void setHighlight(boolean[] highlight) {
        this.highlight = highlight;
    }

    /**
     * Given a name and a query, returns a boolean array
     * in which the query matches the name
     * <p>
     * This is useful for searching commands. The amount
     * that a name matches a query can be sorted by length
     * of the resulting array using {@link Arrays#compare(boolean[], boolean[])}
     * <p>
     * Ignores all characters in which {@link Character#isLetterOrDigit(char)}
     * returns false in the query
     * <p>
     * If over-matched, returns null
     * <p>
     * Ex.
     * parse("ABC", "a c") returns [0,2]
     *
     * @param name  the name to search
     * @param query the query to search for
     * @return the indices
     */
    public static boolean[] parse(String name, String query) {
        boolean[] list = new boolean[name.length()];
        // The char array of the name
        char[] n = name.toLowerCase().toCharArray();
        // The char array of the query
        char[] q = query.toLowerCase().toCharArray();
        // The index of n
        int i = 0;
        // The index of q
        int j = 0;
        while (i < n.length) {
            // Find next query char that is a letter or digit
            while (j < q.length && !Character.isLetterOrDigit(q[j])) j++;
            // Break if the end of query is reached
            if (j == q.length) break;
            // Find the next name char that matches the query char
            while (i < n.length && n[i] != q[j]) i++;
            // Return null if the name cursor reached the end
            if (i == n.length) return null;
            // Add to the index list
            list[i] = true;
            i++;
            j++;
        }
        return list;
    }

    public static int compare(boolean[] a, boolean[] b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return Integer.compare(count(a), count(b));
    }

    private static int count(boolean[] a) {
        int c = 0;
        for (boolean b : a) if (b) c++;
        return c;
    }
}
