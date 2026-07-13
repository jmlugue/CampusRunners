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

class ApiClient {

    fun interface Callback {
        fun onComplete(response: JSONObject)
    }

    private val executor: ExecutorService = Executors.newFixedThreadPool(3)
    private val mainHandler = Handler(Looper.getMainLooper())

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
        return try {
            var urlText = Config.API_BASE_URL + endpoint
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
            JSONObject(text)
        } catch (e: Exception) {
            JSONObject().apply {
                put("success", false)
                put("message", "Connection failed: ${e.message}")
            }
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
}
