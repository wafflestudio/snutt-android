package com.wafflestudio.snutt2.network.error

class ErrorParsedHttpException(
    val body: ErrorBodyDto,
) : Exception(body.message)
