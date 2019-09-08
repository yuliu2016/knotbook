package kb.core.data

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
    Double, String
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
                if (guess != null && guess.isDouble()) {
                    colType = ColType.Double
                }
            }
            when (colType) {
                ColType.Double ->
                    DoubleColumn(colName, records.map { record ->
                        record[colIndex]?.toDouble() ?: Double.NaN
                    }.toDoubleArray())

                ColType.String ->
                    StringColumn(colName, records.map { record ->
                        record[colIndex]
                    }.toTypedArray())
            }
        }

        return DataFrame(columns)
    }
}
