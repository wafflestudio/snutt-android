package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.network.dto.SimpleTableDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * SNUTTStorage 가 [SimpleTableDto] 대신 [SimpleTableLocalEntity] 를 저장하도록 바뀌었으므로,
 * 기존 사용자 기기에 저장된 JSON 이 [SimpleTableLocalEntity] 로도 동일하게 역직렬화되어야 한다.
 * 앱 본체와 동일하게 [KotlinJsonAdapterFactory] 기반 reflection 직렬화를 사용한다.
 * SimpleTable 은 Map<String, SimpleTable> 형태로 저장되므로 Map 직렬화 호환성도 검증.
 */
class SimpleTableLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(SimpleTableLocalEntity::class.java)
    private val dtoAdapter = moshi.adapter(SimpleTableDto::class.java)

    @Test
    fun `legacy SimpleTableDto JSON deserializes into equivalent SimpleTableLocalEntity`() {
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

    @Test
    fun `SimpleTableLocalEntity serializes to byte-equivalent JSON as SimpleTableDto`() {
        val cases = listOf(
            SimpleTableDto(
                id = "abc",
                year = 2024,
                semester = 1,
                title = "내 시간표",
                updatedAt = "2024-03-01",
                totalCredit = 18,
                isPrimary = true,
            ) to SimpleTableLocalEntity(
                id = "abc",
                year = 2024,
                semester = 1,
                title = "내 시간표",
                updatedAt = "2024-03-01",
                totalCredit = 18,
                isPrimary = true,
            ),
            SimpleTableDto(
                id = "no-credit",
                year = 2025,
                semester = 2,
                title = "기본",
                updatedAt = "",
                totalCredit = null,
                isPrimary = false,
            ) to SimpleTableLocalEntity(
                id = "no-credit",
                year = 2025,
                semester = 2,
                title = "기본",
                updatedAt = "",
                totalCredit = null,
                isPrimary = false,
            ),
        )

        cases.forEach { (dto, entity) ->
            assertEquals(dtoAdapter.toJson(dto), entityAdapter.toJson(entity))
        }
    }

    @Test
    fun `Map of SimpleTable serializes byte-equivalent between DTO and LocalEntity`() {
        val entityMapType = Types.newParameterizedType(Map::class.java, String::class.java, SimpleTableLocalEntity::class.java)
        val dtoMapType = Types.newParameterizedType(Map::class.java, String::class.java, SimpleTableDto::class.java)
        val entityMapAdapter = moshi.adapter<Map<String, SimpleTableLocalEntity>>(entityMapType)
        val dtoMapAdapter = moshi.adapter<Map<String, SimpleTableDto>>(dtoMapType)

        val entityMap = mapOf(
            "a" to SimpleTableLocalEntity("a", 2024, 1, "t1", "u1", 15, true),
            "b" to SimpleTableLocalEntity("b", 2024, 2, "t2", "u2", null, false),
        )
        val dtoMap = mapOf(
            "a" to SimpleTableDto("a", 2024, 1, "t1", "u1", 15, true),
            "b" to SimpleTableDto("b", 2024, 2, "t2", "u2", null, false),
        )

        assertEquals(dtoMapAdapter.toJson(dtoMap), entityMapAdapter.toJson(entityMap))
    }

    @Test
    fun `round-trip serialization preserves SimpleTableLocalEntity`() {
        val originals = listOf(
            SimpleTableLocalEntity("a", 2024, 1, "t", "u", 15, true),
            SimpleTableLocalEntity("b", 2025, 2, "t2", "", null, false),
        )

        originals.forEach { original ->
            val parsed = entityAdapter.fromJson(entityAdapter.toJson(original))
            assertEquals(original, parsed)
        }
    }
}
