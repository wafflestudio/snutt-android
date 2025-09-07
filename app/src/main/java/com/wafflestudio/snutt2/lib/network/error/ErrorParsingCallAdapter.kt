package com.wafflestudio.snutt2.lib.network.error

import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

class ErrorParsingCallAdapter<R, T : Any>(
    private val delegate: CallAdapter<R, T>,
    private val serializer: Serializer,
) : CallAdapter<R, T> {
    override fun responseType(): Type = delegate.responseType()

    override fun adapt(call: Call<R>): T {
        return delegate.adapt(ErrorParsingCall(call, serializer))
    }
}

private class ErrorParsingCall<T>(
    private val delegate: Call<T>,
    private val serializer: Serializer,
) : Call<T> by delegate {

    override fun enqueue(callback: Callback<T>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callback.onResponse(call, response)
                } else {
                    val exception = parseError(response)
                    callback.onFailure(call, exception)
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(call, t)
            }
        },)
    }

    override fun execute(): Response<T> {
        val response = delegate.execute()
        if (!response.isSuccessful) {
            throw parseError(response)
        }
        return response
    }

    private fun parseError(response: Response<T>): Exception {
        val errorBody = response.errorBody()?.string() ?: ""

        if (errorBody.isNotEmpty()) {
            try {
                return serializer.deserialize<ErrorParsedHttpException>(
                    errorBody,
                    ErrorParsedHttpException::class.java,
                )
            } catch (_: Exception) {
                // 파싱 실패 시 HttpException 으로 fallback
            }
        }

        return HttpException(response)
    }

    override fun clone(): Call<T> = ErrorParsingCall(delegate.clone(), serializer)
}
