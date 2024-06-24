package com.wafflestudio.snutt2.core.network

import retrofit2.HttpException
import retrofit2.Response

class ErrorParsedHttpException(
    response: Response<*>,
    val errorDTO: ErrorDTO? = null,
) : HttpException(response)
