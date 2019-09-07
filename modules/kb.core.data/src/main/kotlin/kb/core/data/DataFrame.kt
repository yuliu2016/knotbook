package kb.core.data

/**
 * DataFrame based on Krangl
 */
interface DataFrame {
    val columns: List<DataColumn<Any>>
}