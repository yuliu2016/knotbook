package kb.service.api.ui;

import javafx.scene.Node;
import kb.service.api.array.IntArrayList;

import java.util.Arrays;

public class OptionItem {
    private String name;
    private String info;
    private Node graphic;
    private int[] highlight;

    /**
     * Creates a new item
     *
     * @param name      the name of the item
     * @param info      additional info
     * @param graphic   a graphic item, like an icon or a checkbox
     * @param highlight the characters to be highlighted
     */
    public OptionItem(String name, String info, Node graphic, int[] highlight) {
        this.name = name;
        this.info = info;
        this.graphic = graphic;
        this.highlight = highlight;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public Node getGraphic() {
        return graphic;
    }

    public int[] getHighlight() {
        return highlight;
    }

    /**
     * Given a name and a query, returns the indices
     * in which the query matches the name
     * <p>
     * This is useful for searching commands. The amount
     * that a name matches a query can be sorted by length
     * of the resulting array using {@link Arrays#compare(int[], int[])}
     * <p>
     * Ignores all characters in which {@link Character#isLetterOrDigit(char)}
     * returns false in the query
     * <p>
     * If over-matched, returns null
     * <p>
     * Ex.
     * parse("ABC", "a c") returns [0,2]
     * <p>
     * Algorithm:
     * 1. Find the next query char in q
     * 2. Go through the remaining chars of n
     * 3. Set the cursor index to the result
     *
     * @param name  the name to search
     * @param query the query to search for
     * @return the indices
     */
    public static int[] parse(String name, String query) {
        IntArrayList list = new IntArrayList();
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
            list.append(i);
            j++;
        }
        return list.copy();
    }
}
