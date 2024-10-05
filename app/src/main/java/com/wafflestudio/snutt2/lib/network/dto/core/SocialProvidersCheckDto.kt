package com.wafflestudio.snutt2.lib.network.dto.core

import com.wafflestudio.snutt2.core.network.model.SocialProvidersCheck

data class SocialProvidersCheckDto(
    val local: Boolean,
    val facebook: Boolean,
    val google: Boolean,
    val kakao: Boolean,
    val apple: Boolean,
)

fun SocialProvidersCheck.toExternalModel() = SocialProvidersCheckDto(
    local = local,
    facebook = facebook,
    google = google,
    kakao = kakao,
    apple = apple,
)