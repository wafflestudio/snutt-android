package com.wafflestudio.snutt2.components.compose.snackbar

interface CustomSnackBarData {
    val message: String
    val actionLabel: String?
    val duration: CustomSnackBarDuration

    fun performAction()
    fun dismiss()
}
