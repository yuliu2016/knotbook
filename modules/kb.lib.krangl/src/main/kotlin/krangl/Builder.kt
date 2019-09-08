package krangl

import krangl.ArrayUtils.handleListErasure
import krangl.util.asDF


// todo javadoc example needed
/** Create a data-frame from a list of objects */
fun <T> Iterable<T>.deparseRecords(mapping: (T) -> DataFrameRow) = DataFrame.fromRecords(this, mapping)

internal typealias DeparseFormula<T> = T.(T) -> Any?

inline fun <reified T> Iterable<T>.deparseRecords(vararg mapping: Pair<String, DeparseFormula<T>>): DataFrame {
    //    val revMapping = mapping.toMap().entries.associateBy({ it.value }) { it.key }
//    val mappings = mapOf<String, Any?>().toMutableMap().apply { putAll(mapping) }

    val function = { record: T ->
        mapping.toMap().map { (name, deparse) -> name to deparse(record, record) }.toMap()
    }
    return DataFrame.fromRecords(this, function)
}


infix fun <T> String.with(that: DeparseFormula<T>) = Pair(this, that)


/** Create a data-frame from a list of objects */
fun <T> DataFrame.Companion.fromRecords(records: Iterable<T>, mapping: (T) -> DataFrameRow): DataFrame {
    val rowData = records.map { mapping(it) }
    val columnNames = mapping(records.first()).keys

    val columnData = columnNames.map { it to emptyList<Any?>().toMutableList() }.toMap()

    for (record in rowData) {
        columnData.forEach { colName, colData -> colData.add(record[colName]) }
    }

    return columnData.map { (name, data) -> handleListErasure(name, data) }.asDF()
}


/**
 * Create a new data frame in place.
 *
 */
fun dataFrameOf(vararg header: String) = InplaceDataFrameBuilder(header.toList())


/**
 * Create a new data frame in place.
 *
 */
fun dataFrameOf(header: Iterable<String>) = InplaceDataFrameBuilder(header.toList())


/**
 * Create a new data-frame from a list of `DataCol` instances
 */
fun dataFrameOf(vararg columns: DataCol): DataFrame = SimpleDataFrame(*columns)


/** Create a new data-frame from a records encoded as key-value maps.
 *
 * Column types will be inferred from the value types.
 */
fun dataFrameOf(rows: Iterable<DataFrameRow>): DataFrame {
    val colNames = rows.first().keys

    return colNames.map { colName ->
        val colData = rows.map { it[colName] }
        handleListErasure(colName, colData)
    }.let { dataFrameOf(*it.toTypedArray()) }
}


/**
 * Create a new data frame in place.
 *
 */
fun DataFrame.Companion.builder(vararg header: String) = dataFrameOf(*header)


// tbd should we expose this as public API?
internal fun SimpleDataFrame.addColumn(dataCol: DataCol): SimpleDataFrame =
        SimpleDataFrame(cols.toMutableList().apply { add(dataCol) })


class InplaceDataFrameBuilder(private val header: List<String>) {


    operator fun invoke(args: Iterable<Any?>): DataFrame {
        return invoke(*args.toList().toTypedArray())
    }


    operator fun invoke(args: Sequence<Any?>): DataFrame {
        return invoke(*args.toList().toTypedArray())
    }

    operator fun invoke(vararg tblData: Any?): DataFrame {
        //        if(tblData.first() is Iterable<Any?>) {
        //            tblData = tblData.first() as Iterable<Any?>
        //        }

        // is the data vector compatible with the header dimension?
        require(header.isNotEmpty() && tblData.size.rem(header.size) == 0) {
            "data dimension ${header.size} is not compatible with length of data vector ${tblData.size}"
        }

        // 1) break into columns
        val rawColumns: List<List<Any?>> = tblData.toList()
                .mapIndexed { i, any -> i.rem(header.size) to any }
                .groupBy { it.first }.values.map {
            it.map { it2 -> it2.second }
        }


        // 2) infer column type by peeking into column data
        val tableColumns = header.zip(rawColumns).map {
            handleListErasure(it.first, it.second)
        }

        require(tableColumns.map { it.length }.distinct().size == 1) {
            "Provided data does not coerce to tabular shape"
        }

        // 3) bind into data-frame
        return SimpleDataFrame(tableColumns)
    }


    //    operator fun invoke(values: List<Any?>): DataFrame {
    //        return invoke(values.toTypedArray())
    //    }

}