package com.wafflestudio.snutt2.lib.network

interface DisplayMessageResolver {
    fun getDisplayMessage(error: DomainError): String
}
