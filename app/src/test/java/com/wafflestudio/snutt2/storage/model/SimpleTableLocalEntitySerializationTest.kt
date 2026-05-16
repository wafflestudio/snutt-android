package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 기존 사용자 기기 SharedPreference 에 저장된 JSON 이 [SimpleTableLocalEntity] 로 정확히 역직렬화되는지 검증.
 * 이 fixture 의 JSON 모양 = SharedPreference 직렬화 ABI.
 */
class SimpleTableLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(SimpleTableLocalEntity::class.java)

    @Test
    fun `legacy JSON deserializes into equivalent SimpleTableLocalEntity`() {
        val fixtures = listOf(
            """{"_id":"abc123","year":2024,"semester":1,"title":"내 시간표","updated_at":"2024-03-01T10:00:00Z","total_credit":18,"isPrimary":true}""" to
                SimpleTableLocalEntity(
                    id = "abc123",
                    year = 2024,
                    semester = 1,
                    title = "내 시간표",
                    updatedAt = "2024-03-01T10:00:00Z",
                    totalCredit = 18,
                    isPrimary = true,
                ),
            """{"_id":"xyz","year":2025,"semester":2,"title":"기본","updated_at":"","total_credit":null,"isPrimary":false}""" to
                SimpleTableLocalEntity(
                    id = "xyz",
                    year = 2025,
                    semester = 2,
                    title = "기본",
                    updatedAt = "",
                    totalCredit = null,
                    isPrimary = false,
                ),
            """{"_id":"no-primary","year":2023,"semester":1,"title":"t","updated_at":"u","total_credit":10}""" to
                SimpleTableLocalEntity(
                    id = "no-primary",
                    year = 2023,
                    semester = 1,
                    title = "t",
                    updatedAt = "u",
                    totalCredit = 10,
                    isPrimary = false,
                ),
        )

        fixtures.forEach { (json, expected) ->
            assertEquals(expected, entityAdapter.fromJson(json))
        }
    }
}
