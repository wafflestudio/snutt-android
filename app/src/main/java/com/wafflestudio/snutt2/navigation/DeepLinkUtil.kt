package com.wafflestudio.snutt2.navigation

import NavigationDestination
import kotlin.reflect.full.findAnnotation

inline fun <reified T : NavigationDestination> getDeepLinkPath(): String? {
    return T::class.findAnnotation<DeepLinkPath>()?.path
}
