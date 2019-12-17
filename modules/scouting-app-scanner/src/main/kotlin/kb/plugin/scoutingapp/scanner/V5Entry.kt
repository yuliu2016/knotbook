package kb.plugin.scoutingapp.scanner

import kb.plugin.scoutingapp.api.Board
import kb.plugin.scoutingapp.api.v5.V5DataPoint
import kb.plugin.scoutingapp.api.v5.V5Entry
import kb.plugin.scoutingapp.api.v5.toBoard

data class DecodedEntry(override val encoded: String) : V5Entry {
    private val split = encoded.split(":")
    override val match = split[0]
    override val team = split[1]
    override val scout = split[2]
    override val board = split[3].toBoard() ?: Board.R1
    override val timestamp = split[4].toInt(16)
    override val undone = split[5].toInt()
    override val comments = split[7]
    override val dataPoints = emptyList<V5DataPoint>()
    override fun count(type: Int) = dataPoints.count { it.type == type }
    override fun lastValue(type: Int) = dataPoints.lastOrNull { it.type == type }
    override fun focused(type: Int, time: Int) = dataPoints.any { it.type == type && it.time == time }
}
