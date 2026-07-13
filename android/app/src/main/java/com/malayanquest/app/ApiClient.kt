package com.malayanquest.app

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ApiClient(initialBaseUrl: String = Config.DEFAULT_API_BASE_URL) {

    fun interface Callback {
        fun onComplete(response: JSONObject)
    }

    private val executor: ExecutorService = Executors.newFixedThreadPool(3)
    private val mainHandler = Handler(Looper.getMainLooper())
    @Volatile
    private var apiBaseUrl: String = normalizeBaseUrl(initialBaseUrl)

    fun setBaseUrl(value: String) {
        apiBaseUrl = normalizeBaseUrl(value)
    }

    fun getBaseUrl(): String = apiBaseUrl

    fun get(endpoint: String, params: Map<String, String>?, callback: Callback) {
        executor.execute {
            val result = request("GET", endpoint, params, null)
            mainHandler.post { callback.onComplete(result) }
        }
    }

    fun post(endpoint: String, body: JSONObject?, callback: Callback) {
        executor.execute {
            val result = request("POST", endpoint, null, body)
            mainHandler.post { callback.onComplete(result) }
        }
    }

    private fun request(
        method: String,
        endpoint: String,
        params: Map<String, String>?,
        body: JSONObject?
    ): JSONObject {
        var connection: HttpURLConnection? = null
        val baseUrl = apiBaseUrl
        return try {
            var urlText = baseUrl + endpoint
            if (method == "GET" && !params.isNullOrEmpty()) {
                urlText += "?" + encodeParams(params)
            }

            val url = URL(urlText)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("Accept", "application/json")

            if (method == "POST") {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                val outputStream = connection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
                writer.write(body?.toString() ?: "{}")
                writer.flush()
                writer.close()
                outputStream.close()
            }

            val stream = if (connection.responseCode >= 400) {
                connection.errorStream
            } else {
                connection.inputStream
            }
            val text = readStream(stream)
            val trimmed = text.trim()

            if (trimmed.isBlank()) {
                return errorResponse("Empty server response from $endpoint.")
            }

            if (!trimmed.startsWith("{")) {
                val responseType = when {
                    trimmed.startsWith("<!doctype", ignoreCase = true) || trimmed.startsWith("<html", ignoreCase = true) -> "HTML"
                    else -> "non-JSON"
                }
                return errorResponse(
                    "Server returned $responseType instead of JSON for $endpoint. Check that the PHP file exists, Apache/MySQL are running, and the API URL is correct. Current API URL: $baseUrl"
                )
            }

            JSONObject(trimmed)
        } catch (e: Exception) {
            errorResponse("Connection failed: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }

    private fun encodeParams(params: Map<String, String>): String {
        return params.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }
    }

    private fun readStream(stream: InputStream?): String {
        if (stream == null) {
            return """{"success":false,"message":"Empty server response."}"""
        }
        val reader = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
        val text = reader.readText()
        reader.close()
        return text
    }

    private fun errorResponse(message: String): JSONObject {
        return JSONObject().apply {
            put("success", false)
            put("message", message)
        }
    }

    companion object {
        fun normalizeBaseUrl(value: String): String {
            val trimmed = value.trim()
            if (trimmed.isBlank()) {
                return Config.DEFAULT_API_BASE_URL
            }
            return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        }
    }
}
