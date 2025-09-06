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

fun CustomSnackBarStatus.copy(data: CustomSnackBarData? = null): CustomSnackBarStatus {
    return if (data == null) {
        this
    } else {
        when (this) {
            is CustomSnackBarStatus.FadeInOrBetween -> {
                CustomSnackBarStatus.FadeInOrBetween(data = data)
            }

            else -> {
                this
            }
        }
    }
}
