package com.wafflestudio.snutt2

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.DisplayMetrics
import com.wafflestudio.snutt2.model.TagType

/**
 * Created by makesource on 2016. 1. 24..
 */
object SNUTTUtils {
    //    @JvmField
//    var context: Context? = null
    fun wdayToNumber(wday: String): Int {
        if (wday == "월") return 0
        if (wday == "화") return 1
        if (wday == "수") return 2
        if (wday == "목") return 3
        if (wday == "금") return 4
        if (wday == "토") return 5
        return if (wday == "일") 6 else -1
    }

    @JvmStatic
    fun numberToWday(wday: Int): String? {
        when (wday) {
            0 -> return "월"
            1 -> return "화"
            2 -> return "수"
            3 -> return "목"
            4 -> return "금"
            5 -> return "토"
            6 -> return "일"
        }
        return null
    }

    fun numberToTime(num: Float): String {
        val hour = 8 + num.toInt()
        val minute: String
        minute = if (Math.floor(num.toDouble()) == num.toDouble()) "00" else "30"
        return "$hour:$minute"
    }

    fun numberToTimeAdjusted(time : Float, len : Float) : String {
        var adjustedTime = ((8+time+len)*60).toInt()

        when (len) {
            1f -> adjustedTime -= 10
            1.5f -> adjustedTime -= 15
            2f -> adjustedTime -= 10
            3f -> adjustedTime -= 10
            4.5f -> adjustedTime -= 15
        }

        val hour = adjustedTime / 60
        val minute = adjustedTime % 60

        return if(minute==0) "$hour:00"
        else "$hour:$minute"
    }

    fun getTimeList(from: Int, to: Int): Array<String?> {
        val list = arrayOfNulls<String>(to - from + 1)
        for (i in from..to) list[i - from] = numberToTime(i / 2f)
        return list
    }

    @JvmStatic
    fun zeroStr(number: Int): String {
        return if (number < 10) "0$number" else "" + number
    }

    // dp to px
    @JvmStatic
    fun Context.dp2px(dp: Float): Float {
        val resources = this.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    // px to dp
    fun Context.px2dp(px: Float): Float {
        val resources = this.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    // sp to px
    fun Context.sp2px(sp: Float): Float {
        val scaledDensity = this.resources.displayMetrics.scaledDensity
        return sp * scaledDensity
    }

    // px to sp
    fun Context.px2sp(px: Float): Float {
        val scaledDensity = this.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    @JvmStatic
    val Context.displayWidth: Float
        get() = this.resources.displayMetrics.widthPixels.toFloat()

    @JvmStatic
    val Context.displayHeight: Float
        get() = this.resources.displayMetrics.heightPixels.toFloat()

    fun getTagColor(type: TagType?): Int {
        return when (type) {
            TagType.ACADEMIC_YEAR -> Color.rgb(229, 68, 89)
            TagType.CLASSIFICATION -> Color.rgb(245, 141, 61)
            TagType.CREDIT -> Color.rgb(166, 217, 48)
            TagType.DEPARTMENT -> Color.rgb(27, 208, 200)
            TagType.INSTRUCTOR -> Color.rgb(29, 153, 232)
            TagType.CATEGORY -> Color.rgb(175, 86, 179)
            else -> Color.RED
        }
    }
}
