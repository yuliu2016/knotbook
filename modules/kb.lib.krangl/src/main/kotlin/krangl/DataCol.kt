package krangl

import krangl.util.joinToMaxLengthString
import java.util.*

abstract class DataCol(val name: String) {  // tbd why not: Iterable<Any> ??


    open infix operator fun plus(something: Number): DataCol = plusInternal(something)
    open infix operator fun plus(something: DataCol): DataCol = plusInternal(something)
    open infix operator fun plus(something: Iterable<*>): DataCol = plusInternal(ArrayUtils.handleArrayErasure("foo", something.toList().toTypedArray()))
    protected open fun plusInternal(something: Any): DataCol = throw UnsupportedOperationException()


    open infix operator fun minus(something: Number): DataCol = minusInternal(something)
    open infix operator fun minus(something: DataCol): DataCol = minusInternal(something)
    protected open fun minusInternal(something: Any): DataCol = throw UnsupportedOperationException()

    open infix operator fun div(something: Number): DataCol = divInternal(something)
    open infix operator fun div(something: DataCol): DataCol = divInternal(something)
    protected open fun divInternal(something: Any): DataCol = throw UnsupportedOperationException()


    open infix operator fun times(something: Number): DataCol = timesInternal(something)
    open infix operator fun times(something: DataCol): DataCol = timesInternal(something)
    protected open infix fun timesInternal(something: Any): DataCol = throw UnsupportedOperationException()


    infix operator fun plus(something: String): DataCol = when (this) {
        is StringCol -> values.map { naAwarePlus(it, something) }
        else -> values().map { (it?.toString() ?: MISSING_VALUE) + something }
    }.toTypedArray().let { StringCol(tempColumnName(), it) }


    operator fun unaryMinus(): DataCol = this * -1

    open operator fun not(): DataCol = throw UnsupportedOperationException()

    abstract fun values(): Array<*>

    abstract val length: Int

    override fun toString(): String {
        val prefix = "$name [${getColumnType(this)}][$length]: "
        val peek = values().take(255).asSequence()
                .joinToMaxLengthString(maxLength = PRINT_MAX_WIDTH - prefix.length, transform = createValuePrinter(PRINT_MAX_DIGITS))

        return prefix + peek
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataCol) return false

        if (name != other.name) return false
        if (length != other.length) return false
        //        http://stackoverflow.com/questions/35272761/how-to-compare-two-arrays-in-kotlin
        if (!Arrays.equals(values(), other.values())) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + length + Arrays.hashCode(values())
        return result
    }

    operator fun get(index: Int) = values()[index]
}