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
    override fun getDisplayMessage(error: DomainError): String {
        return when (error) {
            is NetworkDisconnect -> context.getString(R.string.error_no_network)
            is ServerFault -> context.getString(R.string.error_server_fault)
            is NoAdminPrivilege -> context.getString(R.string.error_no_admin_privilege)
            is UnknownApp -> context.getString(R.string.error_unknown_app)
            is WrongApiKey -> context.getString(R.string.error_wrong_api_key)
            is NoUserToken -> context.getString(R.string.error_no_user_token)
            is WrongUserToken -> context.getString(R.string.error_wrong_user_token)
            is Unknown -> context.getString(R.string.error_unknown)
            else -> error.displayMessage
        }
    }
}
