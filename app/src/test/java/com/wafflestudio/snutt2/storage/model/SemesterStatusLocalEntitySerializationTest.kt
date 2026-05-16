package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.network.dto.CourseBookDto
import com.wafflestudio.snutt2.network.dto.SemesterStatusDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * SNUTTStorage 가 [SemesterStatusDto] 대신 [SemesterStatusLocalEntity] 를 저장하도록 바뀌었으므로,
 * 기존 사용자 기기에 저장된 JSON 이 [SemesterStatusLocalEntity] 로도 동일하게 역직렬화되어야 한다.
 * 앱 본체와 동일하게 [KotlinJsonAdapterFactory] 기반 reflection 직렬화를 사용한다.
 */
class SemesterStatusLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(SemesterStatusLocalEntity::class.java)
    private val dtoAdapter = moshi.adapter(SemesterStatusDto::class.java)

    @Test
    fun `legacy SemesterStatusDto JSON deserializes into equivalent SemesterStatusLocalEntity`() {
        val fixtures = listOf(
            """{"current":{"semester":2,"year":2024},"next":{"semester":3,"year":2024}}""" to
                SemesterStatusLocalEntity(
                    current = CourseBookLocalEntity(semester = 2, year = 2024),
                    next = CourseBookLocalEntity(semester = 3, year = 2024),
                ),
            """{"current":null,"next":{"semester":1,"year":2025}}""" to
                SemesterStatusLocalEntity(
                    current = null,
                    next = CourseBookLocalEntity(semester = 1, year = 2025),
                ),
            """{"next":{"semester":4,"year":2023}}""" to
                SemesterStatusLocalEntity(
                    current = null,
                    next = CourseBookLocalEntity(semester = 4, year = 2023),
                ),
        )

        fixtures.forEach { (json, expected) ->
            assertEquals(expected, entityAdapter.fromJson(json))
        }
    }

    @Test
    fun `SemesterStatusLocalEntity serializes to byte-equivalent JSON as SemesterStatusDto`() {
        val cases = listOf(
            SemesterStatusDto(
                current = CourseBookDto(semester = 1, year = 2024),
                next = CourseBookDto(semester = 2, year = 2024),
            ) to SemesterStatusLocalEntity(
                current = CourseBookLocalEntity(semester = 1, year = 2024),
                next = CourseBookLocalEntity(semester = 2, year = 2024),
            ),
            SemesterStatusDto(
                current = null,
                next = CourseBookDto(semester = 3, year = 2025),
            ) to SemesterStatusLocalEntity(
                current = null,
                next = CourseBookLocalEntity(semester = 3, year = 2025),
            ),
        )

        cases.forEach { (dto, entity) ->
            assertEquals(dtoAdapter.toJson(dto), entityAdapter.toJson(entity))
        }
    }

    @Test
    fun `round-trip serialization preserves SemesterStatusLocalEntity`() {
        val originals = listOf(
            SemesterStatusLocalEntity(
                current = CourseBookLocalEntity(semester = 2, year = 2024),
                next = CourseBookLocalEntity(semester = 3, year = 2024),
            ),
            SemesterStatusLocalEntity(
                current = null,
                next = CourseBookLocalEntity(semester = 1, year = 2026),
            ),
        )

        originals.forEach { original ->
            val parsed = entityAdapter.fromJson(entityAdapter.toJson(original))
            assertEquals(original, parsed)
        }
    }
}
