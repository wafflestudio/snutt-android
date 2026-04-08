package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError

class FakeDisplayMessageResolver : DisplayMessageResolver {
    override fun getDisplayTitle(error: DomainError): String = error.displayTitle
    override fun getDisplayMessage(error: DomainError): String = error.displayMessage
}
