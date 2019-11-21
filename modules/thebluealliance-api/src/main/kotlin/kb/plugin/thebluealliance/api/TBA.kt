package kb.plugin.thebluealliance.api

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * The Blue Alliance API
 */
class TBA(
        private val authKey: String,
        private val userAgent: String = "KnotBook"
) {

    private fun getTBAString(requestURL: String): String {
        try {
            val url = URL("https://www.thebluealliance.com/api/v3$requestURL")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.useCaches = false
            conn.setRequestProperty("X-TBA-Auth-Key", authKey)
            conn.setRequestProperty("User-Agent", userAgent)
            return conn.inputStream.bufferedReader().use { br -> br.readText() }
        } catch (e: Throwable) {
            throw e
        }
    }

    internal fun get(requestURL: String): JSONObject {
        return JSONObject(getTBAString(requestURL))
    }

    internal fun getArray(requestURL: String): JSONArray {
        return JSONArray(getTBAString(requestURL))
    }
}