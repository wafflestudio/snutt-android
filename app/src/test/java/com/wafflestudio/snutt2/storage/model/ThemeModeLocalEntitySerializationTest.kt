package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.ui.theme.ThemeMode
import org.junit.Test
import kotlin.test.assertEquals

/**
 * SNUTTStorage 가 [ThemeMode] 대신 [ThemeModeLocalEntity] 를 저장하도록 바뀌었으므로,
 * 기존 사용자 기기에 저장된 enum name JSON 이 동일하게 역직렬화되어야 한다.
 * Moshi 는 enum 을 `.name` 으로 직렬화하므로 LocalEntity 의 enum value 이름이 도메인과 동일해야 한다.
 */
class ThemeModeLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(ThemeModeLocalEntity::class.java)
    private val dtoAdapter = moshi.adapter(ThemeMode::class.java)

    @Test
    fun `legacy ThemeMode JSON deserializes into equivalent ThemeModeLocalEntity`() {
        val fixtures = listOf(
            "\"DARK\"" to ThemeModeLocalEntity.DARK,
            "\"LIGHT\"" to ThemeModeLocalEntity.LIGHT,
            "\"AUTO\"" to ThemeModeLocalEntity.AUTO,
        )

        fixtures.forEach { (json, expected) ->
            assertEquals(expected, entityAdapter.fromJson(json))
        }
    }

    @Test
    fun `ThemeModeLocalEntity serializes to byte-equivalent JSON as ThemeMode for every value`() {
        ThemeMode.entries.forEach { mode ->
            assertEquals(dtoAdapter.toJson(mode), entityAdapter.toJson(mode.toLocalEntity()))
        }
    }

    @Test
    fun `round-trip serialization preserves ThemeModeLocalEntity for every value`() {
        ThemeModeLocalEntity.entries.forEach { entity ->
            assertEquals(entity, entityAdapter.fromJson(entityAdapter.toJson(entity)))
        }
    }
}
