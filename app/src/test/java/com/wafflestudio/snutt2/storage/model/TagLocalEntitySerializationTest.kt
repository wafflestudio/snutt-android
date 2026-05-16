package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 기존 사용자 기기 SharedPreference 에 저장된 JSON 이 [TagLocalEntity] 로 정확히 역직렬화되는지 검증.
 *
 * 이 fixture 의 JSON 모양 = SharedPreference 직렬화 ABI.
 * 누군가 미래에 @param:Json 매핑 / 필드명 / enum value name 을 무심결에 변경하면 이 테스트가 깨져서
 * "기존 사용자 데이터 호환성이 깨졌다" 를 자동으로 알린다.
 *
 * 앱 본체와 동일하게 Moshi codegen 기반 직렬화 (ApplicationModule.provideMoshi 와 일치).
 */
class TagLocalEntitySerializationTest {

    private val moshi = Moshi.Builder().build()
    private val entityAdapter = moshi.adapter(TagLocalEntity::class.java)

    @Test
    fun `legacy JSON deserializes into equivalent TagLocalEntity`() {
        val fixtures = listOf(
            """{"type":"SORT_CRITERIA","name":"평점 높은 순"}""" to
                TagLocalEntity(TagTypeLocalEntity.SORT_CRITERIA, "평점 높은 순"),
            """{"type":"CLASSIFICATION","name":"공통"}""" to
                TagLocalEntity(TagTypeLocalEntity.CLASSIFICATION, "공통"),
            """{"type":"DEPARTMENT","name":"컴퓨터공학부"}""" to
                TagLocalEntity(TagTypeLocalEntity.DEPARTMENT, "컴퓨터공학부"),
            """{"type":"ACADEMIC_YEAR","name":"1학년"}""" to
                TagLocalEntity(TagTypeLocalEntity.ACADEMIC_YEAR, "1학년"),
            """{"type":"CREDIT","name":"3학점"}""" to
                TagLocalEntity(TagTypeLocalEntity.CREDIT, "3학점"),
            """{"type":"TIME","name":"빈 시간대로 검색"}""" to
                TagLocalEntity(TagTypeLocalEntity.TIME, "빈 시간대로 검색"),
            """{"type":"CATEGORY","name":"인문학"}""" to
                TagLocalEntity(TagTypeLocalEntity.CATEGORY, "인문학"),
            """{"type":"CATEGORY_PRE2025","name":"인문학"}""" to
                TagLocalEntity(TagTypeLocalEntity.CATEGORY_PRE2025, "인문학"),
            """{"type":"ETC","name":"영어진행 강의"}""" to
                TagLocalEntity(TagTypeLocalEntity.ETC, "영어진행 강의"),
        )

        fixtures.forEach { (json, expected) ->
            assertEquals(expected, entityAdapter.fromJson(json))
        }
    }
}
