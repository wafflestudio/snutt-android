package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 기존 사용자 기기 SharedPreference 에 저장된 JSON 이 [SemesterStatusLocalEntity] 로 정확히 역직렬화되는지 검증.
 * 이 fixture 의 JSON 모양 = SharedPreference 직렬화 ABI.
 */
class SemesterStatusLocalEntitySerializationTest {

    private val moshi = Moshi.Builder().build()
    private val entityAdapter = moshi.adapter(SemesterStatusLocalEntity::class.java)

    @Test
    fun `legacy JSON deserializes into equivalent SemesterStatusLocalEntity`() {
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
}
