package kb.service.api.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static kb.service.api.array.TableUtil.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableUtilTest {

    @Test
    void columnIndexTest() {
        Assertions.assertEquals(columnIndexToString(0), "A");
        Assertions.assertEquals(columnIndexToString(5), "F");
        Assertions.assertEquals(columnIndexToString(26), "AA");
        Assertions.assertThrows(IllegalArgumentException.class, () -> columnIndexToString(-1));
    }

    @Test
    void arrayReferenceAndEqualTest() {
        Reference r1 = new RangeReference(20, 1, 3, 2, 2);
        Reference r2 = new MultiCellReference(17);
        Reference result = arrayReference(r1, r2);
        MultiCellReference golden = new MultiCellReference(17, 23, 24, 43, 44);
        assertTrue(referenceEquals(result, golden));
    }

    @Test
    void splitTest() {
    }
}
