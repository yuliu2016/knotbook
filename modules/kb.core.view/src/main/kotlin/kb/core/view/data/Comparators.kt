@file:Suppress("DuplicatedCode")

package kb.core.view.data

import kb.service.api.util.NaturalOrderComparator
import krangl.*

fun DataCol.ascendingComparator(): Comparator<Int> = when (this) {
    is DoubleCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> {
                va.compareTo(vb)
            }
        }
    }
    is IntCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> va.compareTo(vb)
        }
    }
    is BooleanCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> va.compareTo(vb)
        }
    }
    is StringCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> NaturalOrderComparator.compareNaturally(va, vb)
        }
    }
    else -> throw UnsupportedOperationException()
}

fun DataCol.descendingComparator(): Comparator<Int> = when (this) {
    is DoubleCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> vb.compareTo(va)
        }
    }
    is IntCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> vb.compareTo(va)
        }
    }
    is BooleanCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> vb.compareTo(va)
        }
    }
    is StringCol -> Comparator { a, b ->
        val va = values[a]
        val vb = values[b]
        when {
            va === vb -> 0
            va == null -> 1 // a > b
            vb == null -> -1 // a < b
            else -> NaturalOrderComparator.compareNaturally(vb, va)
        }
    }
    else -> throw UnsupportedOperationException()
}