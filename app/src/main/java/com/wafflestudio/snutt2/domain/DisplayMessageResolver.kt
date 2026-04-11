package com.wafflestudio.snutt2.domain

interface DisplayMessageResolver {
    fun getDisplayTitle(error: DomainError): String
    fun getDisplayMessage(error: DomainError): String
}
