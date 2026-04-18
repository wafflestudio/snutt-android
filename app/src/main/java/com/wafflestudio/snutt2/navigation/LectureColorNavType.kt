package com.wafflestudio.snutt2.navigation

import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import com.wafflestudio.snutt2.domain.model.LectureColor
import kotlinx.serialization.json.Json

val LectureColorNavType = object : NavType<LectureColor>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: LectureColor) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): LectureColor = BundleCompat.getParcelable(bundle, key, LectureColor::class.java)!!

    override fun parseValue(value: String): LectureColor = Json.decodeFromString(value)

    override fun serializeAsValue(value: LectureColor): String = Json.encodeToString(LectureColor.serializer(), value)
}
