package com.wafflestudio.snutt2.lib.network

interface DisplayMessageResolver {
    fun getDisplayTitle(error: DomainError): String
    fun getDisplayMessage(error: DomainError): String
}
