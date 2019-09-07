//@file:Suppress("unused")

package krangl

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.*
import java.net.URI
import java.net.URL
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
Methods to read and write tables into/from DataFrames
 * see also https://commons.apache.org/proper/commons-csv/ for other implementations
 * https://github.com/databricks/spark-csv
 * https://examples.javacodegeeks.com/core-java/apache/commons/csv-commons/writeread-csv-files-with-apache-commons-csv-example/

 */


enum class ColType {
    Int, Long, Double, Boolean, String, Guess
}

private fun asStream(fileOrUrl: String) = (if (isURL(fileOrUrl)) {
    URL(fileOrUrl).toURI()
} else {
    File(fileOrUrl).toURI()
}).toURL().openStream()

internal fun isURL(fileOrUrl: String): Boolean = listOf("http:", "https:", "ftp:").any { fileOrUrl.startsWith(it) }


@JvmOverloads
fun DataFrame.Companion.readCSV(
        fileOrUrl: String,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        asStream(fileOrUrl),
        format = format,
        colTypes = colTypes,
        isCompressed = listOf("gz", "zip").contains(fileOrUrl.split(".").last())
)


@JvmOverloads
fun DataFrame.Companion.readTSV(
        fileOrUrl: String,
        format: CSVFormat = CSVFormat.TDF.withHeader(),
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        inStream = asStream(fileOrUrl),
        format = format,
        colTypes = colTypes,
        isCompressed = listOf("gz", "zip").contains(fileOrUrl.split(".").last())
)

@JvmOverloads
fun DataFrame.Companion.readTSV(
        file: File,
        format: CSVFormat = CSVFormat.TDF.withHeader(),
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        FileInputStream(file),
        format = format,
        colTypes = colTypes,
        isCompressed = guessCompressed(file)
)


@JvmOverloads
fun DataFrame.Companion.readCSV(
        file: File,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        inStream = FileInputStream(file),
        format = format,
        colTypes = colTypes,
        isCompressed = listOf("gz", "zip").contains(file.extension)
)


private fun guessCompressed(file: File) = listOf("gz", "zip").contains(file.extension)


// http://stackoverflow.com/questions/9648811/specific-difference-between-bufferedreader-and-filereader
fun DataFrame.Companion.readDelim(
        uri: URI,
        //                                hasHeader:Boolean =true,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
        isCompressed: Boolean = uri.toURL().toString().endsWith(".gz"),
        colTypes: Map<String, ColType> = mapOf()
): DataFrame {

    val inputStream = uri.toURL().openStream()
    val streamReader = if (isCompressed) {
        // http://stackoverflow.com/questions/1080381/gzipinputstream-reading-line-by-line
        val gzip = GZIPInputStream(inputStream)
        InputStreamReader(gzip)
    } else {
        InputStreamReader(inputStream)
    }

    return readDelim(
            BufferedReader(streamReader),
            format = format,
            colTypes = colTypes
    )
}

//http://stackoverflow.com/questions/5200187/convert-inputstream-to-bufferedreader
fun DataFrame.Companion.readDelim(
        inStream: InputStream,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
        isCompressed: Boolean = false,
        colTypes: Map<String, ColType> = mapOf()
) =
        if (isCompressed) {
            InputStreamReader(GZIPInputStream(inStream))
        } else {
            BufferedReader(InputStreamReader(inStream, "UTF-8"))
        }.run {
            readDelim(this, format, colTypes = colTypes)
        }


fun DataFrame.Companion.readDelim(
        reader: Reader,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
        colTypes: Map<String, ColType> = mapOf(),
        skip: Int = 0
): DataFrame {

    val formatWithNullString = if (format.isNullStringSet) {
        format
    } else {
        format.withNullString(MISSING_VALUE)
    }

    var reader1 = reader
    if (skip > 0) {
        reader1 = BufferedReader(reader1)
        repeat(skip) { reader1.readLine() }
    }

    val csvParser = formatWithNullString.parse(reader1)
    val records = csvParser.records

    val columnNames = csvParser.headerMap?.keys
            ?: (1..records[0].count()).map { index -> "X${index}" }

    // Make column names unique when reading them + unit test
    val uniqueNames = columnNames
            .withIndex()
            .groupBy { it.value }
            .flatMap { (grpName, columns) ->
                columns
                        .mapIndexed { index, indexedValue ->
                            indexedValue.index to (grpName + if (index > 2) "_${index + 2}" else "")
                        }
            }
            .sortedBy { it.first }.map { it.second }


    //    csvParser.headerMap.keys.pmap{colName ->
    val cols = uniqueNames.mapIndexed { colIndex, colName ->
        val defaultColType = colTypes[".default"] ?: ColType.Guess

        val colType = colTypes[colName] ?: defaultColType

        dataColFactory(colName, colIndex, colType, records)
    }

    return SimpleDataFrame(cols)
}


val MISSING_VALUE = "NA"

// NA aware conversions
internal fun String.naAsNull(): String? = if (this == MISSING_VALUE) null else this

internal fun String?.nullAsNA(): String = this ?: MISSING_VALUE

internal fun String?.cellValueAsBoolean(): Boolean? {
    if (this == null) return null

    var cellValue: String? = toUpperCase()

    cellValue = if (cellValue == "F") "FALSE" else cellValue
    cellValue = if (cellValue == "T") "TRUE" else cellValue

    if (!listOf("TRUE", "FALSE", null).contains(cellValue)) throw NumberFormatException("invalid boolean cell value")

    return cellValue?.toBoolean()
}

internal fun guessColType(firstElements: List<String>): ColType =
        when {
            isBoolCol(firstElements) -> ColType.Boolean
            isIntCol(firstElements) -> ColType.Int
            isLongCol(firstElements) -> ColType.Long
            isDoubleCol(firstElements) -> ColType.Double
            else -> ColType.String
        }


internal fun dataColFactory(colName: String, colIndex: Int, colType: ColType, records: MutableList<CSVRecord>): DataCol =
        when (colType) {
            // see https://github.com/holgerbrandl/krangl/issues/10
            ColType.Int -> try {
                IntCol(colName, records.map { it[colIndex]?.toInt() })
            } catch (e: NumberFormatException) {
                StringCol(colName, records.map { it[colIndex] })
            }
            ColType.Long -> try {
                LongCol(colName, records.map { it[colIndex]?.toLong() })
            } catch (e: NumberFormatException) {
                StringCol(colName, records.map { it[colIndex] })
            }

            ColType.Double -> DoubleCol(colName, records.map { it[colIndex]?.toDouble() })

            ColType.Boolean -> BooleanCol(colName, records.map { it[colIndex]?.cellValueAsBoolean() })

            ColType.String -> StringCol(colName, records.map { it[colIndex] })

            ColType.Guess -> dataColFactory(colName, colIndex, guessColType(peekCol(colIndex, records)), records)
        }


// TODO add missing value support with user defined string (e.g. NA here) here

internal fun isDoubleCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.toDouble() }; true
} catch (e: NumberFormatException) {
    false
}

internal fun isIntCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.toInt() }; true
} catch (e: NumberFormatException) {
    false
}

internal fun isLongCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.toLong() }; true
} catch (e: NumberFormatException) {
    false
}

internal fun isBoolCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.cellValueAsBoolean() }; true
} catch (e: NumberFormatException) {
    false
}


internal fun peekCol(colIndex: Int, records: List<CSVRecord>, peekSize: Int = 100) = records
        .asSequence()
        .mapIndexed { rowIndex, _ -> records[rowIndex][colIndex] }
        .filterNotNull()
        .take(peekSize)
        .toList()


fun DataFrame.writeTSV(
        file: File,
        format: CSVFormat = CSVFormat.TDF.withHeader(*names.toTypedArray())
) = writeCSV(file, format)


fun DataFrame.writeCSV(
        file: File,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(*names.toTypedArray())
) {
    @Suppress("NAME_SHADOWING")
    val format = if (format.run { header != null && header.size == 0 }) {
        warning("[krangl] Adding missing column names to csv format")
        format.withHeader(*names.toTypedArray())
    } else {
        format
    }

    val compress: Boolean = listOf("gz", "zip").contains(file.extension)

    val p = if (!compress) PrintWriter(file) else
        BufferedWriter(OutputStreamWriter(GZIPOutputStream(FileOutputStream(file))))

    //initialize CSVPrinter object
    val csvFilePrinter = CSVPrinter(p, format)

    // write records
    for (record in rowData()) {
        csvFilePrinter.printRecord(record)
    }

    p.flush()
    p.close()
}