@file:Suppress("unused")

package kb.tba.client

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonBase
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


private suspend fun TBA.getTBAString(
        requestURL: String
): String = suspendCoroutine { cont ->
    thread {
        try {
            val url = URL("https://www.thebluealliance.com/api/v3$requestURL")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.useCaches = false
            conn.setRequestProperty("X-TBA-Auth-Key", authKey)
            conn.setRequestProperty("User-Agent", userAgent)
            val response = conn.inputStream.bufferedReader().use { br -> br.readText() }
            cont.resume(response)
        } catch (e: Throwable) {
            cont.resumeWithException(e)
        }
    }
}

private suspend fun TBA.getParsed(requestURL: String): JsonBase {
    val response = getTBAString(requestURL)
    return Parser.default().parse(StringBuilder(response)) as JsonBase
}

internal suspend fun TBA.get(requestURL: String): JsonObject {
    return getParsed(requestURL) as JsonObject
}

internal suspend fun TBA.getArray(requestURL: String): JsonArray<*> {
    return getParsed(requestURL) as JsonArray<*>
}

internal inline fun <T> JsonArray<*>.mapToList(action: (JsonObject) -> T): List<T> {
    val result = ArrayList<T>()
    for (i in 0 until size) result.add(action(get(i) as JsonObject))
    return result
}

internal fun JsonObject.genericArray(fieldName: String): JsonArray<*>? {
    return get(fieldName) as JsonArray<*>?
}

internal fun JsonObject.stringList(fieldName: String): List<String>? {
    return array<String>(fieldName)?.toList()
}

internal fun JsonObject.doubleList(fieldName: String): List<Double>? {
    return array<Double>(fieldName)?.toList()
}

internal fun JsonObject.intList(fieldName: String): List<Int>? {
    return array<Int>(fieldName)?.toList()
}

internal fun JsonObject.booleanList(fieldName: String): List<Boolean>? {
    return array<Boolean>(fieldName)?.toList()
}

internal fun JsonObject.objList(fieldName: String): List<Map<String, Any?>>? {
    return array<JsonObject>(fieldName)?.toList()
}