@file:Suppress("unused")

package knotbook.tba.client

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonBase
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString


object ConfigSystem {
    fun getTBAKey(): String? = TODO()
    fun getUserAgent(): String = TODO()
}

private var authKey = ConfigSystem.getTBAKey()
private var loadingCache = false
private var savingCache = false

fun TBA.hasKey() = authKey == null

fun TBA.setKey(key: String) {
    authKey = key
}

fun TBA.setOptions(
    loadCache: Boolean = true,
    saveCache: Boolean = true
) {
    loadingCache = loadCache
    savingCache = saveCache
}

private suspend fun TBA.getParsed(requestURL: String): JsonBase {
    val response = Fuel
        .get("http://www.thebluealliance.com/api/v3$requestURL")
        .header("X-TBA-Auth-Key" to authKey!!, "User-Agent" to ConfigSystem.getUserAgent())
        .awaitString()
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