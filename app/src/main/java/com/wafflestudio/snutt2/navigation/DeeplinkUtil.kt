package com.wafflestudio.snutt2.navigation

import NavigationDestination
import kotlin.reflect.full.createInstance

inline fun <reified T : NavigationDestination> getDeepLinkPath(): String? {
    return T::class.objectInstance?.deepLinkPath // data object 인 경우 바로 가져온다
        ?: T::class.createInstance().deepLinkPath // data class 인 경우 인스턴스를 생성하고 가져온다. 인자 필요없는 생성자가 있어야 함.
}
