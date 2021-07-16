package com.wafflestudio.snutt2.lib

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto


fun Int.toDayString(context: Context): String {
    val days = arrayOf(
        context.getString(R.string.common_day_mon),
        context.getString(R.string.common_day_tue),
        context.getString(R.string.common_day_wed),
        context.getString(R.string.common_day_thu),
        context.getString(R.string.common_day_fri),
        context.getString(R.string.common_day_sat),
        context.getString(R.string.common_day_sun),
    )
    return days.getOrElse(this) { "-" }
}
