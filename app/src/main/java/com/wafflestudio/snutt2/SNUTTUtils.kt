package com.wafflestudio.snutt2

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.DisplayMetrics
import kotlin.math.roundToInt

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

    fun numberToEndTimeAdjusted(time: Float, len: Float): String {
        val totalMinute = ((8 + time + len) * 60).roundToInt()
        val totalMinuteAdjusted = when (len) {
            1f -> totalMinute - 10
            1.5f -> totalMinute - 15
            2f -> totalMinute - 10
            3f -> totalMinute - 10
            4.5f -> totalMinute - 10
            else -> totalMinute
        }
        val hour = totalMinuteAdjusted / 60
        val minute = totalMinuteAdjusted % 60

        return String.format("%02d:%02d", hour, minute)
    }

    fun String.semesterStringToLong(): Long = when (this) {
        "SPRING" -> 1L
        "SUMMER" -> 2L
        "AUTUMN" -> 3L
        "WINTER" -> 4L
        else -> 0L
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
}
