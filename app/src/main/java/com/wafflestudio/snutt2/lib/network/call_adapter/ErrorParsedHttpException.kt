package com.wafflestudio.snutt2.lib.network.call_adapter

import com.wafflestudio.snutt2.lib.network.ErrorDTO
import retrofit2.HttpException
import retrofit2.Response

class ErrorParsedHttpException(
    response: Response<*>,
    val errorDTO: ErrorDTO? = null
) : HttpException(response)
