package kb.service.api.array;

import java.util.Comparator;

import static kb.service.api.array.TableArray.*;

@SuppressWarnings("DuplicatedCode")
public class ComparisonHelper {
    public static Comparator<Integer> ascendingComparator(TableArray array, int col) {
        if (col < 0 || col > array.cols) {
            throw new IllegalArgumentException();
        }
        return (a, b) -> {
            int ai = a * array.cols + col;
            int bi = b * array.cols + col;
            int av = array.mode[ai];
            int bv = array.mode[bi];
            if (av == MODE_NULL) {
                return bv == MODE_NULL ? 0 : 1;
            } else {
                if (bv == MODE_NULL) {
                    return -1;
                } else {
                    if (av == MODE_INT || av == MODE_FLOAT) {
                        return bv == MODE_INT || bv == MODE_FLOAT ? Float
                                .compare(array.num[ai], array.num[bi]) : 1;
                    } else {
                        return bv == MODE_INT || bv == MODE_FLOAT ? -1 : NaturalOrderComparator
                                .compareNaturally(array.str.get(ai), array.str.get(bi));
                    }
                }
            }
        };
    }

    public static Comparator<Integer> descendingComparator(TableArray array, int col) {
        if (col < 0 || col > array.cols) {
            throw new IllegalArgumentException();
        }
        return (a, b) -> {
            int ai = a * array.cols + col;
            int bi = b * array.cols + col;
            int av = array.mode[ai];
            int bv = array.mode[bi];
            if (av == MODE_NULL) {
                return bv == MODE_NULL ? 0 : 1;
            } else {
                if (bv == MODE_NULL) {
                    return -1;
                } else {
                    if (av == MODE_INT || av == MODE_FLOAT) {
                        return bv == MODE_INT || bv == MODE_FLOAT ? Float
                                .compare(array.num[bi], array.num[ai]) : -1;
                    } else {
                        return bv == MODE_INT || bv == MODE_FLOAT ? 1 : NaturalOrderComparator
                                .compareNaturally(array.str.get(bi), array.str.get(ai));
                    }
                }
            }
        };
    }
}
