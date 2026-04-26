package ug.ac.ndejje.cbc_teachers_toolkit.data.remote

import java.net.HttpURLConnection
import java.net.URL

object SimpleHttpClient {
    fun get(url: String, connectTimeoutMs: Int = 10_000, readTimeoutMs: Int = 20_000): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = connectTimeoutMs
            readTimeout = readTimeoutMs
        }

        return try {
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream.bufferedReader().use { it.readText() }
            if (code !in 200..299) {
                throw IllegalStateException("HTTP $code: $body")
            }
            body
        } finally {
            connection.disconnect()
        }
    }
}
