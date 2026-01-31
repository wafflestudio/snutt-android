package com.wafflestudio.snutt2.navigation

import com.wafflestudio.snutt2.views.NavigationDestination
import kotlin.reflect.full.findAnnotation

inline fun <reified T : NavigationDestination> getDeepLinkPath(): String? {
    return T::class.findAnnotation<DeepLinkPath>()?.path
}
