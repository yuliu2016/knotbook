@file:Suppress("unused")

package kb.core.view

data class SortColumn(val sortType: SortType, val index: Int) {
    override fun equals(other: Any?): Boolean {
        return other is SortColumn && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }
}

enum class SortType {
    Ascending, Descending
}


data class ColorScale(val index: Int, val sortType: SortType, val r: Int, val g: Int, val b: Int) {
    override fun equals(other: Any?): Boolean {
        return other is ColorScale && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }

    companion object {
        val green = ColorScale(0, SortType.Ascending, 96, 192, 144)
        val orange = ColorScale(0, SortType.Ascending, 255, 144, 0)
        val blue = ColorScale(0, SortType.Ascending, 100, 170, 255)
        val read = ColorScale(0, SortType.Ascending, 255, 108, 108)
    }
}