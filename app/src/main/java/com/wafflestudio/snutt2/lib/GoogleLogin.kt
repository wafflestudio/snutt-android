package com.wafflestudio.snutt2.lib

import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

fun handleSignIn(result: GetCredentialResponse): GoogleIdTokenCredential? {
    // Handle the successfully returned credential.
    val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract id to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    return googleIdTokenCredential
                } catch (e: GoogleIdTokenParsingException) {
                    return null
                    // Log.e(TAG, "Received an invalid google id token response", e)
                }
            } else {
                return null
                // Catch any unrecognized credential type here.
                // Log.e(TAG, "Unexpected type of credential")
            }
        }

        else -> {
            return null
            // Catch any unrecognized credential type here.
            // Log.e(TAG, "Unexpected type of credential")
        }
    }
}
