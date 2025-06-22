package com.wafflestudio.snutt2.lib.network

import android.content.Context
import com.wafflestudio.snutt2.R

class DisplayMessageResolver(
    private val context: Context,
) {
    fun getDisplayMessage(error: DomainError): String {
        return when (error) {
            is NetworkDisconnect -> context.getString(R.string.error_no_network)
            is ServerFault -> context.getString(R.string.error_server_fault)
            is NoAdminPrivilege -> context.getString(R.string.error_no_admin_privilege)
            is UnknownApp -> context.getString(R.string.error_unknown_app)
            is AuthError.WrongApiKey -> context.getString(R.string.error_wrong_api_key)
            is AuthError.NoUserToken -> context.getString(R.string.error_no_user_token)
            is AuthError.WrongUserToken -> context.getString(R.string.error_wrong_user_token)
            is Unknown -> context.getString(R.string.error_unknown)
            else -> error.displayMessage
        }
    }
}
