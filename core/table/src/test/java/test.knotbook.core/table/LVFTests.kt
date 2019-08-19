package test.knotbook.core.table

import knotbook.core.table.LinearVirtualFlow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `sumIntoOrExpand on setCellCount`() {
        val flow = LinearVirtualFlow()
        flow.minSize = 30.0
        flow.setCellCount(4)
        assertTrue(flow.cellPos.contentEquals(doubleArrayOf(0.0, 30.0, 60.0, 90.0, 120.0)))
    }
}