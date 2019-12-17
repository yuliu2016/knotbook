package kb.plugin.scoutingapp.api.v6;

import java.util.ArrayList;
import java.util.List;

/**
 * Like V5DataPoint, but with more time accuracy and more robust
 */
@SuppressWarnings("unused")
public class V6DataPoint {
    /**
     * The type of the Data Point tracked
     */
    int type;

    /**
     * The Data Point Value, between 0 and 15
     * For multiple choice or ratings
     */
    int value;

    /**
     * The time in 0.01 second intervals since the start of the match
     */
    int time;

    public V6DataPoint(int type, int value, int time) {
        if (type < 0 || type >= 64 || value < 0 || value >= 16 || time < 0 || time >= 16384) {
            throw new IllegalArgumentException("Data Out of Bounds");
        }
        this.type = type;
        this.value = value;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public int getTime() {
        return time;
    }

    public double getTimeInSeconds() {
        return time / 100.0;
    }

    @Override
    public String toString() {
        return "V6DataPoint{" +
                "type=" + type +
                ", value=" + value +
                ", time=" + getTimeInSeconds() +
                '}';
    }

    /**
     * 24-bit data point encoder, into 4 base64 chars
     * 000000 000000 000000 000000
     * bit 1-6: type
     * bit 7-10: value
     * bit 11-24: time
     */
    public static void appendEncode(V6DataPoint dp, StringBuilder b) {
        if (dp == null) {
            throw new IllegalArgumentException("DataPoint cannot be null");
        }
        b.append(toBase64(dp.type));
        b.append(toBase64(dp.value << 2 | (dp.time & 0b11 << 12) >> 12));
        b.append(toBase64((dp.time & 0b111111 << 6) >> 6));
        b.append(toBase64(dp.time & 0b111111));
    }

    public static String encode(V6DataPoint dp) {
        StringBuilder builder = new StringBuilder();
        appendEncode(dp, builder);
        return builder.toString();
    }

    public static List<V6DataPoint> decode(String s) {
        if (s == null || s.length() % 4 != 0) {
            throw new IllegalArgumentException("Invalid Encode String");
        }
        List<V6DataPoint> dp = new ArrayList<>();
        for (int i = 0; i < s.length(); i += 4) {
            dp.add(decode(s, i));
        }
        return dp;
    }

    public static V6DataPoint decode(String s, int i) {
        int a = fromBase64(s.charAt(i));
        int b = fromBase64(s.charAt(i + 1));
        int c = fromBase64(s.charAt(i + 2));
        int d = fromBase64(s.charAt(i + 3));
        return new V6DataPoint(a, (b & 0b111100) >> 2, (b & 0b11) << 12 | c << 6 | d);
    }

    static int fromBase64(int ch) {
        if (ch == '/') return 63;
        if (ch == '+') return 62;
        if (ch >= 48 && ch < 58) return ch + 4;
        if (ch >= 65 && ch < 91) return ch - 65;
        if (ch >= 97 && ch < 123) return ch - 71;
        throw new IllegalArgumentException("Invalid Base64 Input");
    }

    static char toBase64(int i) {
        if (i < 0 || i > 63) {
            throw new IllegalArgumentException("Invalid Integer Input");
        }
        return i < 26 ? (char) (i + 65)
                : i < 52 ? (char) (i + 71)
                : i < 62 ? (char) (i - 4)
                : i == 62 ? '+'
                : '/';
    }
}
