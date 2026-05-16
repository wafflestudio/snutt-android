package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.network.dto.ClassTimeDto
import com.wafflestudio.snutt2.network.dto.ColorDto
import com.wafflestudio.snutt2.network.dto.LectureDto
import com.wafflestudio.snutt2.network.dto.LectureReviewDto
import com.wafflestudio.snutt2.network.dto.TableDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * SNUTTStorage 가 [TableDto] 대신 [TableLocalEntity] 를 저장하도록 바뀌었으므로,
 * 기존 사용자 기기에 저장된 JSON 이 [TableLocalEntity] 로도 동일하게 역직렬화되어야 한다.
 * 앱 본체와 동일하게 [KotlinJsonAdapterFactory] 기반 reflection 직렬화를 사용한다.
 *
 * TableLocalEntity 는 LectureLocalEntity, ClassTimeLocalEntity, ColorLocalEntity,
 * LectureReviewLocalEntity 와 중첩되므로 중첩 직렬화 호환성을 함께 검증.
 */
class TableLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(TableLocalEntity::class.java)
    private val dtoAdapter = moshi.adapter(TableDto::class.java)

    private fun sampleDto() = TableDto(
        id = "t1",
        year = 2024,
        semester = 1,
        title = "내 시간표",
        lectureList = listOf(
            LectureDto(
                id = "L1",
                lecture_id = "ORIG_L1",
                classification = "전공",
                department = "컴퓨터공학부",
                academic_year = "3",
                course_number = "M1522.000900",
                lecture_number = "001",
                course_title = "운영체제",
                credit = 3,
                class_time_json = listOf(
                    ClassTimeDto(day = 1, place = "302-308", id = "ct1", startMinute = 570, endMinute = 645),
                ),
                instructor = "홍길동",
                quota = 60,
                freshmanQuota = 0,
                remark = "",
                category = null,
                categoryPre2025 = null,
                colorIndex = 2,
                color = ColorDto(fgRaw = "#FFFFFF", bgRaw = "#000000"),
                registrationCount = 50,
                wasFull = false,
                review = LectureReviewDto(id = "ev1", rating = 4.5, reviewCount = 10),
            ),
        ),
        updatedAt = "2024-03-01T10:00:00Z",
        totalCredit = 3,
        theme = 1,
        themeId = null,
        isPrimary = true,
    )

    private fun sampleEntity() = TableLocalEntity(
        id = "t1",
        year = 2024,
        semester = 1,
        title = "내 시간표",
        lectureList = listOf(
            LectureLocalEntity(
                id = "L1",
                lecture_id = "ORIG_L1",
                classification = "전공",
                department = "컴퓨터공학부",
                academic_year = "3",
                course_number = "M1522.000900",
                lecture_number = "001",
                course_title = "운영체제",
                credit = 3,
                class_time_json = listOf(
                    ClassTimeLocalEntity(day = 1, place = "302-308", id = "ct1", startMinute = 570, endMinute = 645),
                ),
                instructor = "홍길동",
                quota = 60,
                freshmanQuota = 0,
                remark = "",
                category = null,
                categoryPre2025 = null,
                colorIndex = 2,
                color = ColorLocalEntity(fgRaw = "#FFFFFF", bgRaw = "#000000"),
                registrationCount = 50,
                wasFull = false,
                review = LectureReviewLocalEntity(id = "ev1", rating = 4.5, reviewCount = 10),
            ),
        ),
        updatedAt = "2024-03-01T10:00:00Z",
        totalCredit = 3,
        theme = 1,
        themeId = null,
        isPrimary = true,
    )

    @Test
    fun `TableLocalEntity serializes to byte-equivalent JSON as TableDto`() {
        assertEquals(dtoAdapter.toJson(sampleDto()), entityAdapter.toJson(sampleEntity()))
    }

    @Test
    fun `legacy TableDto JSON deserializes into equivalent TableLocalEntity`() {
        val legacyJson = dtoAdapter.toJson(sampleDto())
        val parsed = entityAdapter.fromJson(legacyJson)
        assertEquals(sampleEntity(), parsed)
    }

    @Test
    fun `round-trip serialization preserves TableLocalEntity`() {
        val parsed = entityAdapter.fromJson(entityAdapter.toJson(sampleEntity()))
        assertEquals(sampleEntity(), parsed)
    }

    @Test
    fun `empty lectureList and nullable fields serialize byte-equivalent`() {
        val dto = TableDto(
            id = "empty",
            year = 2023,
            semester = 2,
            title = "",
            lectureList = emptyList(),
            updatedAt = "",
            totalCredit = null,
            theme = 0,
            themeId = "custom-theme-id",
            isPrimary = false,
        )
        val entity = TableLocalEntity(
            id = "empty",
            year = 2023,
            semester = 2,
            title = "",
            lectureList = emptyList(),
            updatedAt = "",
            totalCredit = null,
            theme = 0,
            themeId = "custom-theme-id",
            isPrimary = false,
        )
        assertEquals(dtoAdapter.toJson(dto), entityAdapter.toJson(entity))
    }
}
