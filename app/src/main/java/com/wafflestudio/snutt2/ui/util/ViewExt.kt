package com.wafflestudio.snutt2.ui.util

import android.content.Context
import android.util.TypedValue

fun Int.dp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics,
)

fun Float.dp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics,
)

fun Int.sp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.toFloat(),
    context.resources.displayMetrics,
)

fun Float.sp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    context.resources.displayMetrics,
)

val Context.displayWidth: Float
    get() = this.resources.displayMetrics.widthPixels.toFloat()

val Context.displayHeight: Float
    get() = this.resources.displayMetrics.heightPixels.toFloat()
