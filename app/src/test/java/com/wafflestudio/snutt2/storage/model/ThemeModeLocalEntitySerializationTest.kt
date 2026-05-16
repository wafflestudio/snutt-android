package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 기존 사용자 기기 SharedPreference 에 저장된 enum name JSON 이 [ThemeModeLocalEntity] 로 정확히 역직렬화되는지 검증.
 * Moshi 가 enum 을 `.name` 으로 직렬화하므로 LocalEntity 의 value 이름이 변경되면 호환성 깨짐을 자동 감지.
 */
class ThemeModeLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(ThemeModeLocalEntity::class.java)

    @Test
    fun `legacy JSON deserializes into equivalent ThemeModeLocalEntity`() {
        val fixtures = listOf(
            "\"DARK\"" to ThemeModeLocalEntity.DARK,
            "\"LIGHT\"" to ThemeModeLocalEntity.LIGHT,
            "\"AUTO\"" to ThemeModeLocalEntity.AUTO,
        )

        fixtures.forEach { (json, expected) ->
            assertEquals(expected, entityAdapter.fromJson(json))
        }
    }
}
