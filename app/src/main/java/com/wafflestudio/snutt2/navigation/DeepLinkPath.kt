package com.wafflestudio.snutt2.navigation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DeepLinkPath(val path: String)
