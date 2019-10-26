package kb.core.view

data class SortColumn(val sortType: SortType, val index: Int) {
    override fun equals(other: Any?): Boolean {
        return other is SortColumn && other.index == index
    }

    override fun hashCode(): Int {
        return index
    }
}