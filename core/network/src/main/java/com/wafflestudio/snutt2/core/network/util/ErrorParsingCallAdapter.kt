package com.wafflestudio.snutt2.core.network.util

import com.wafflestudio.snutt2.core.network.model.ErrorDTO
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import retrofit2.*
import java.lang.reflect.Type

class ErrorParsingCallAdapter<R>(
    private val delegation: CallAdapter<R, Any>?,
    @CoreNetwork private val serializer: Serializer,
    private val bodyType: Type,
) : CallAdapter<R, Any> {

    override fun adapt(call: Call<R>): Any {
        return when (val processed = delegation?.adapt(call) ?: call) {
            is Single<*> -> processed.onErrorResumeNext {
                Single.error(parseErrorBody(it))
            }
            is Maybe<*> -> processed.onErrorResumeNext {
                Maybe.error(parseErrorBody(it))
            }
            is Completable -> processed.onErrorResumeNext {
                Completable.error(parseErrorBody(it))
            }
            is Call<*> ->
                @Suppress("UNCHECKED_CAST")
                (processed as Call<R>).let {
                    object : Call<R> by it {
                        override fun enqueue(callback: Callback<R>) {
                            it.enqueue(
                                object : Callback<R> {
                                    override fun onResponse(call: Call<R>, response: Response<R>) {
                                        if (response.isSuccessful.not()) {
                                            callback.onFailure(call, parseErrorBody(response))
                                        } else {
                                            callback.onResponse(call, response)
                                        }
                                    }

                                    override fun onFailure(call: Call<R>, t: Throwable) {
                                        callback.onFailure(call, t)
                                    }
                                },
                            )
                        }
                    }
                }
            else -> processed
        }
    }

    private fun parseErrorBody(throwable: Throwable): Throwable {
        return when (throwable) {
            is HttpException -> {
                return try {
                    parseErrorBody(throwable.response()!!)
                } catch (e: Throwable) {
                    e
                }
            }
            else -> throwable
        }
    }

    private fun parseErrorBody(response: Response<*>): Throwable {
        val errorBodyStr =
            response.errorBody()?.string() ?: return IllegalStateException("ErrorBody is null")
        val errorDTO = kotlin.runCatching {
            serializer.deserialize<ErrorDTO>(
                errorBodyStr,
                ErrorDTO::class.java,
            )
        }.getOrNull()
            ?: return IllegalStateException("ErrorBody parsing failed")
        return ErrorParsedHttpException(
            response,
            errorDTO,
        )
    }

    override fun responseType(): Type {
        return delegation?.responseType() ?: bodyType
    }
}
