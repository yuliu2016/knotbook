package kb.core.data

import kb.service.api.df.DataFrame
import kb.service.api.df.DoubleColumn
import kb.service.api.df.IntColumn
import kb.service.api.df.StringColumn
import org.apache.commons.csv.CSVFormat
import java.io.InputStream
import java.io.Reader

fun readData(
        inStream: InputStream
) {
    readData(inStream.bufferedReader(Charsets.UTF_8))
}

val format: CSVFormat = CSVFormat.DEFAULT
        .withHeader()
        .withNullString("")

enum class ColType {
    Double,
    String,
    Int
}

fun String.isInt(): Boolean {
    try {
        toInt()
    } catch (e: NumberFormatException) {
        return false
    }
    return true
}

fun String.isDouble(): Boolean {
    try {
        toDouble()
    } catch (e: NumberFormatException) {
        return false
    }
    return true
}

// Note, does not capture exceptions
// Very strict format... but it needs to be simple
fun readData(
        reader: Reader
): DataFrame {

    // Make sure the reader is not leaked
    format.parse(reader)!!.use { parser ->

        val columnNames = parser.headerMap!!.keys

        // Parse all the records in the file
        val records = parser.records

        val columns = columnNames.mapIndexed { colIndex, colName ->

            // Defaults to string
            var colType = ColType.String

            if (records.isNotEmpty()) {
                val guess = records[0][colIndex]

                if (guess != null && guess.isInt()) {
                    colType = ColType.Int
                } else if (guess != null && guess.isDouble()) {
                    colType = ColType.Double
                }
            }
            when (colType) {
                ColType.Double ->
                    DoubleColumn(colName, records.map { record ->
                        record[colIndex]?.toDouble() ?: Double.NaN
                    }.toDoubleArray())

                ColType.Int -> IntColumn(colName, records.map { record ->
                    record[colIndex]?.toInt() ?: Integer.MIN_VALUE
                }.toIntArray())

                ColType.String ->
                    StringColumn(colName, records.map { record ->
                        record[colIndex]
                    }.toTypedArray())
            }
        }

        return DataFrame(columns)
    }
}
