package test.knotbook.core.table

import knotbook.core.table.LinearVirtualFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class LVFTests {

    @Test
    fun `setCellCount sets cellCount`() {
        val flow = LinearVirtualFlow()
        flow.setCellCount(10)
        assertEquals(10, flow.cellCount)
    }

    @Test
    fun `total size calculation`() {
        val flow = LinearVirtualFlow()
        flow.minSize = 30.0 // pixels
        flow.setCellCount(20)
        assertEquals(600.0, flow.totalSize)
    }

    @Test
    fun `setCellCount negative count`() {
        val flow = LinearVirtualFlow()
        assertThrows<IllegalArgumentException> {
            flow.setCellCount(-1)
        }
    }
}