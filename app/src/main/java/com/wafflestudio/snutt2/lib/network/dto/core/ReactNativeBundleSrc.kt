package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json

data class ReactNativeBundleSrc(
    @Json(name = "src") val src: Map<String, String>
)