package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 기존 사용자 기기 SharedPreference 에 저장된 JSON 이 [TableLocalEntity] 로 정확히 역직렬화되는지 검증.
 *
 * [TableLocalEntity] 는 [LectureLocalEntity], [ClassTimeLocalEntity], [ColorLocalEntity],
 * [LectureReviewLocalEntity] 로 깊게 중첩되므로 중첩 직렬화 호환성도 함께 커버.
 *
 * 이 fixture 의 JSON 모양 = SharedPreference 직렬화 ABI. 누군가 미래에 어노테이션 / 필드명 / enum value 를
 * 무심결에 변경하면 이 테스트가 깨져 호환성 회귀를 자동 알린다.
 */
class TableLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(TableLocalEntity::class.java)

    @Test
    fun `legacy JSON with full lecture deserializes into equivalent TableLocalEntity`() {
        val json = """{"_id":"t1","year":2024,"semester":1,"title":"내 시간표","lecture_list":[{"_id":"L1","lecture_id":"ORIG_L1","classification":"전공","department":"컴퓨터공학부","academic_year":"3","course_number":"M1522.000900","lecture_number":"001","course_title":"운영체제","credit":3,"class_time_json":[{"day":1,"place":"302-308","_id":"ct1","startMinute":570,"endMinute":645}],"instructor":"홍길동","quota":60,"freshmanQuota":0,"remark":"","colorIndex":2,"color":{"fg":"#FFFFFF","bg":"#000000"},"registrationCount":50,"wasFull":false,"snuttEvLecture":{"evLectureId":"ev1","avgRating":4.5,"evaluationCount":10}}],"updated_at":"2024-03-01T10:00:00Z","total_credit":3,"theme":1,"isPrimary":true}"""

        val expected = TableLocalEntity(
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

        assertEquals(expected, entityAdapter.fromJson(json))
    }

    @Test
    fun `legacy JSON with empty lecture list and custom themeId deserializes into equivalent TableLocalEntity`() {
        val json = """{"_id":"empty","year":2023,"semester":2,"title":"","lecture_list":[],"updated_at":"","theme":0,"themeId":"custom-theme-id","isPrimary":false}"""

        val expected = TableLocalEntity(
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

        assertEquals(expected, entityAdapter.fromJson(json))
    }
}
