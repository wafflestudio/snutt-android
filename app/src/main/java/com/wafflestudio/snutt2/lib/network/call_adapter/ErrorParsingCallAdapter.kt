package com.wafflestudio.snutt2.lib.network.call_adapter

import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import com.wafflestudio.snutt2.lib.network.ErrorDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import retrofit2.*

class ErrorParsingCallAdapter<R>(
    private val delegation: CallAdapter<R, Any>,
    private val serializer: Serializer
) : CallAdapter<R, Any> by delegation {

    override fun adapt(call: Call<R>): Any {
        return when (val processed = delegation.adapt(call)) {
            is Single<*> -> processed.onErrorResumeNext {
                Single.error(parseErrorBody(it))
            }
            is Maybe<*> -> processed.onErrorResumeNext {
                Maybe.error(parseErrorBody(it))
            }
            is Completable -> processed.onErrorResumeNext {
                Completable.error(parseErrorBody(it))
            }
            else -> processed
        }
    }

    private fun parseErrorBody(throwable: Throwable): Throwable {
        return when (throwable) {
            is HttpException -> {
                return try {
                    val parsedError = throwable.response()?.errorBody()?.string()?.let {
                        serializer.deserialize<ErrorDTO>(it, ErrorDTO::class.java)
                    }
                    ErrorParsedHttpException(throwable.response()!!, parsedError)
                } catch (e: Throwable) {
                    e
                }
            }
            else -> throwable
        }
    }
}


