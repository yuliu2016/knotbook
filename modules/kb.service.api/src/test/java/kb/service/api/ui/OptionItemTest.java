package kb.service.api.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OptionItemTest {
    @Test
    void parserTest() {
        Assertions.assertArrayEquals(
                OptionItem.parse("a 3b4", "a 34"),
                new int[]{0, 2, 4}
        );
    }
}
