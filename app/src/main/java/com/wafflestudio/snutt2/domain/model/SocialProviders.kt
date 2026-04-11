package com.wafflestudio.snutt2.domain.model

data class SocialProviders(
    val local: Boolean,
    val facebook: Boolean,
    val google: Boolean,
    val kakao: Boolean,
    val apple: Boolean,
)
