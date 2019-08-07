package knotbook.core.table

@Suppress("unused")
fun columnIndexToString(col: Int): String {
    if (col < 0) {
        return col.toString()
    }
    if (col < 26) {
        return (65 + col).toChar().toString()
    }
    var b = ""
    var n = col
    while (n >= 26) {
        b = (65 + n % 26).toChar() + b
        n /= 26
    }
    b = (64 + n).toChar() + b
    return b
}