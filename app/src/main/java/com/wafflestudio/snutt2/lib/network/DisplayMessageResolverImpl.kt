package com.wafflestudio.snutt2.lib.network

import android.content.Context
import com.wafflestudio.snutt2.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// DisplayMessageResolver는 서버 displayMessage가 없거나, 있는데 클라이언트의 displayMessage와 다른 경우를 커버한다.
// 추후 테스트를 용이하게 하기 위해 interface 분리되어 있음.
class DisplayMessageResolverImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DisplayMessageResolver {
    override fun getDisplayTitle(error: DomainError): String {
        return when (error) {
            is NetworkDisconnect -> context.getString(R.string.error_title_no_network)
            is Unknown -> ""
            else -> error.displayTitle
        }
    }
    override fun getDisplayMessage(error: DomainError): String {
        return when (error) {
            is NetworkDisconnect -> context.getString(R.string.error_no_network)
            is Unknown -> context.getString(R.string.error_unknown)
            else -> error.displayMessage
        }
    }
}
