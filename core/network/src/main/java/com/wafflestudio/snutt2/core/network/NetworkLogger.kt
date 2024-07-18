package com.wafflestudio.snutt2.core.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import okio.IOException
import timber.log.Timber
import java.nio.charset.StandardCharsets
import com.wafflestudio.snutt2.core.database.model.NetworkLog as NetworkLogDatabase

data class NetworkLog(
    val requestMethod: String,
    val requestUrl: String,
    val requestHeader: String,
    val requestBody: String,
    val responseCode: String,
    val responseBody: String,
)

fun Interceptor.Chain.createNewNetworkLog(
    context: Context,
    response: Response,
): NetworkLog {
    try {
        val request = request()
        val jsonPrettyParser = GsonBuilder().setPrettyPrinting().create()

        val requestMethod = request.method
        val requestUrl = request.url.toString()
            .replace(context.getString(R.string.api_server) + "v1/", "/")
            .replace(context.getString(R.string.api_server), "/")
        val requestHeader = request.headers.toString()
        val responseCode = response.code.toString()
        var requestBody = ""
        var responseBody = ""

        try {
            requestBody = request.body?.run {
                val buffer = Buffer()
                writeTo(buffer)
                jsonPrettyParser.toJson(
                    JsonParser.parseString(
                        buffer.readString(contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8),
                    ),
                )
            } ?: ""

            responseBody = response.body?.run {
                GsonBuilder().setPrettyPrinting().create().toJson(
                    JsonParser.parseString(
                        source().buffer.clone().readString(
                            contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8,
                        ),
                    ),
                )
            } ?: ""
        } catch (e: IOException) {
            Timber.d(e)
        }

        return NetworkLog(requestMethod, requestUrl, requestHeader, requestBody, responseCode, responseBody)
    } catch (e: IOException) {
        return createHttpFailLog(e, context)
    }
}

fun Interceptor.Chain.createHttpFailLog(e: IOException, context: Context): NetworkLog {
    val request = request()
    val jsonPrettyParser = GsonBuilder().setPrettyPrinting().create()

    val requestMethod = request.method
    val requestUrl = request.url.toString()
        .replace(context.getString(R.string.api_server) + "v1/", "/")
        .replace(context.getString(R.string.api_server), "/")
    val requestHeader = request.headers.toString()
    var requestBody = ""

    try {
        requestBody = request.body?.run {
            val buffer = Buffer()
            writeTo(buffer)
            jsonPrettyParser.toJson(
                JsonParser.parseString(
                    buffer.readString(contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8),
                ),
            )
        } ?: ""
    } catch (_: IOException) {
    }

    return NetworkLog(requestMethod, requestUrl, requestHeader, requestBody, "FAIL", "HTTP FAILED: $e")
}

fun NetworkLog.toDatabaseModel() = NetworkLogDatabase(
    requestMethod = this.requestMethod,
    requestUrl = this.requestUrl,
    requestHeader = this.requestHeader,
    requestBody = this.requestBody,
    responseCode = this.responseCode,
    responseBody = this.responseBody,
)
