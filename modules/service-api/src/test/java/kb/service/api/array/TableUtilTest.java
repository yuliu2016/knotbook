package kb.service.api.array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TableUtilTest {

    @Test
    void columnIndexTest() {
        Assertions.assertEquals(Tables.columnIndexToString(0), "A");
        Assertions.assertEquals(Tables.columnIndexToString(5), "F");
        Assertions.assertEquals(Tables.columnIndexToString(26), "AA");
        Assertions.assertThrows(IllegalArgumentException.class, () -> Tables.columnIndexToString(-1));
    }

    @Test
    void arrayReferenceAndEqualTest() {
        Reference r1 = new RangeReference(20, 1, 3, 2, 2);
        Reference r2 = new MultiCellReference(17);
        Reference result = Tables.arrayReference(r1, r2);
        MultiCellReference golden = new MultiCellReference(17, 23, 24, 43, 44);
        assertTrue(Tables.referenceEquals(result, golden));
    }

    @Test
    void splitTest() {
    }
}
