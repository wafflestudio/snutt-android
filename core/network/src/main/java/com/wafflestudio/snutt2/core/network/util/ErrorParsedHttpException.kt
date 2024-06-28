package com.wafflestudio.snutt2.core.network.util

import com.wafflestudio.snutt2.core.network.ErrorDTO
import retrofit2.HttpException
import retrofit2.Response

class ErrorParsedHttpException(
    response: Response<*>,
    val errorDTO: ErrorDTO? = null,
) : HttpException(response)
