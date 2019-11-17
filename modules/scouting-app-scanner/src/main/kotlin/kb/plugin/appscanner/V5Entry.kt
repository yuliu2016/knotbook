package kb.plugin.appscanner

import kb.plugin.appscanner.Alliance.Blue
import kb.plugin.appscanner.Alliance.Red

interface V5Entry {
    val match: String
    val team: String
    val scout: String
    val board: Board
    val timestamp: Int
    val encoded: String
    val dataPoints: List<DataPoint>
    val comments: String
    val undone: Int
    fun count(type: Int): Int
    fun lastValue(type: Int): DataPoint?
    fun focused(type: Int, time: Int): Boolean
}


enum class Alliance {
    Red, Blue
}

enum class Board(val alliance: Alliance) {
    R1(Red),
    R2(Red),
    R3(Red),
    B1(Blue),
    B2(Blue),
    B3(Blue),
    RX(Red),
    BX(Blue);
}

fun String.toBoard() = Board.values().firstOrNull { it.name == this }

data class DataPoint(val type: Int, val value: Int, val time: Int) : Iterable<Byte> {
    override fun iterator() = byteArrayOf(type.toByte(), value.toByte(), time.toByte()).iterator()
}

data class DecodedEntry(override val encoded: String) : V5Entry {
    private val split = encoded.split(":")
    override val match = split[0]
    override val team = split[1]
    override val scout = split[2]
    override val board = split[3].toBoard() ?: Board.R1
    override val timestamp = split[4].toInt(16)
    override val undone = split[5].toInt()
    override val comments = split[7]
    override val dataPoints = ArrayList<DataPoint>() /*Base64.getDecoder().decode(split[6]).map { it.toInt() }
            .run { (0 until size / 3).map { DataPoint(get(it * 3), get(it * 3 + 1), get(it * 3 + 2)) } }
            .sortedBy { it.time }*/

    override fun count(type: Int) = dataPoints.count { it.type == type }
    override fun lastValue(type: Int) = dataPoints.lastOrNull { it.type == type }
    override fun focused(type: Int, time: Int) = dataPoints.any { it.type == type && it.time == time }
}
