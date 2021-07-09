package com.wafflestudio.snutt2.lib

import android.content.Context
import androidx.appcompat.app.AlertDialog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialogController @Inject constructor(private val context: Context) {
    fun showAlertDialog(builder: AlertDialog.Builder.() -> Unit) {
    }
}
