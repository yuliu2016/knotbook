package kb.plugin.scoutingapp.api.v6

import kb.plugin.scoutingapp.api.v6.V6DataPoint.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class V6DataPointTest {
    @Test
    fun toBase64Test() {
        assertEquals('A', toBase64(0))
        assertEquals('a', toBase64(26))
        assertEquals('0', toBase64(52))
        assertEquals('+', toBase64(62))
        assertEquals('/', toBase64(63))
        assertThrows<IllegalArgumentException> { toBase64(-1) }
        assertThrows<IllegalArgumentException> { toBase64(64) }
    }

    @Test
    fun fromBase64Test() {
        assertEquals(0, fromBase64('A'.toInt()))
        assertEquals(26, fromBase64('a'.toInt()))
        assertEquals(52, fromBase64('0'.toInt()))
        assertEquals(62, fromBase64('+'.toInt()))
        assertEquals(63, fromBase64('/'.toInt()))
        assertThrows<IllegalArgumentException> { fromBase64(-1) }
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun encoderTest() {
        val dp1 = V6DataPoint(0, 0, 0)
        assertEquals("AAAA", encode(dp1))
        val dp2 = V6DataPoint(27, 0, 0)
        assertEquals("bAAA", encode(dp2))
        val dp3 = V6DataPoint(0, 4, 0)
        assertEquals("AQAA", encode(dp3))
        val dp4 = V6DataPoint(0, 4, 2)
        assertEquals("AQAC", encode(dp4))
        assertThrows<IllegalArgumentException> { encode(null) }
    }
}