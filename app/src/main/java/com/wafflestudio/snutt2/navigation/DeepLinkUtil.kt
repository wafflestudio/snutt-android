package com.wafflestudio.snutt2.navigation

import kotlin.reflect.full.findAnnotation

inline fun <reified T : NavigationDestination> getDeepLinkPath(): String? = T::class.findAnnotation<DeepLinkPath>()?.path
