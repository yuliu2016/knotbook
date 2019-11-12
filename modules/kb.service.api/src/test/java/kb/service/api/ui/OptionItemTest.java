package kb.service.api.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OptionItemTest {
    @Test
    void parserTest() {
        Assertions.assertArrayEquals(
                new boolean[]{true, false, true, false, true},
                OptionItem.parse("a 3b4", "a 34")
        );
    }

    @Test
    void parserTest2() {
        Assertions.assertArrayEquals(
                new boolean[]{true, true, true, false, false, false, false, false, false, false, false},
                OptionItem.parse("application", "app")
        );
    }
}
