package kb.service.api.array;

import java.util.Comparator;

import static kb.service.api.array.TableArray.*;

public class ComparisonHelper {
    public static Comparator<Integer> ascendingComparator(TableArray array, int col) {
        if (col < 0 || col > array.cols) {
            throw new IllegalArgumentException();
        }
        return (a, b) -> {
            int ai = a * array.cols + col;
            int bi = b * array.cols + col;
            int av = array.mode.value[ai];
            int bv = array.mode.value[bi];
            if (av == MODE_NULL) {
                return bv == MODE_NULL ? 0 : 1;
            } else {
                if (bv == MODE_NULL) {
                    return -1;
                } else {
                    if (av == MODE_INT || av == MODE_FLOAT) {
                        return bv == MODE_INT || bv == MODE_FLOAT ? Float
                                .compare(array.num.value[ai], array.num.value[bi]) : 1;
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
            int av = array.mode.value[ai];
            int bv = array.mode.value[bi];
            if (av == MODE_NULL) {
                return bv == MODE_NULL ? 0 : 1;
            } else {
                if (bv == MODE_NULL) {
                    return -1;
                } else {
                    if (av == MODE_INT || av == MODE_FLOAT) {
                        return bv == MODE_INT || bv == MODE_FLOAT ? Float
                                .compare(array.num.value[bi], array.num.value[ai]) : -1;
                    } else {
                        return bv == MODE_INT || bv == MODE_FLOAT ? 1 : NaturalOrderComparator
                                .compareNaturally(array.str.get(bi), array.str.get(ai));
                    }
                }
            }
        };
    }
}
