package com.wafflestudio.snutt2.deeplink

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.wafflestudio.snutt2.views.NavigationDestination

val NavArgumentMap = mapOf(
    NavigationDestination.Home to listOf(
        navArgument("timetableId") {
            type = NavType.StringType
            nullable = true
        }, navArgument("lectureId") {
            type = NavType.StringType
            nullable = true
        }, navArgument("year") {
            type = NavType.StringType
            nullable = true
        }, navArgument("semester") {
            type = NavType.StringType
            nullable = true
        }, navArgument("handled") {
            type = NavType.BoolType
            defaultValue = false
        },
    ),
)

val NavRouteMap = NavArgumentMap.mapValues { (key, value) ->
    StringBuilder()
        .apply {
            append(key)
            value.forEachIndexed { idx, namedArgument ->
                if (idx == 0) append('?') else append('&')
                append("${namedArgument.name}={${namedArgument.name}}")
            }
        }
        .toString()
}

