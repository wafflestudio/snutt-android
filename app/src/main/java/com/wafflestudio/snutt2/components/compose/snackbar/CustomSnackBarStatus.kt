package com.wafflestudio.snutt2.components.compose.snackbar

sealed interface CustomSnackBarStatus {
    data object InVisible : CustomSnackBarStatus
    data class FadeInOrBetween(val data: CustomSnackBarData) : CustomSnackBarStatus
    data object FadeOut : CustomSnackBarStatus
}

fun CustomSnackBarStatus.dismiss() {
    when (this) {
        is CustomSnackBarStatus.FadeInOrBetween -> {
            this.data.dismiss()
        }
        else -> {}
    }
}
