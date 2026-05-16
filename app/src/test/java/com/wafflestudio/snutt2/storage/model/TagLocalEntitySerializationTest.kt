package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.network.dto.TagDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * SNUTTStorage 가 [TagDto] 대신 [TagLocalEntity] 를 저장하도록 바뀌었으므로,
 * 기존 사용자 기기에 저장된 JSON 이 [TagLocalEntity] 로도 동일하게 역직렬화되어야 한다.
 * 앱 본체와 동일하게 [KotlinJsonAdapterFactory] 기반 reflection 직렬화를 사용한다 (ApplicationModule.provideMoshi 와 일치).
 */
class TagLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(TagLocalEntity::class.java)
    private val dtoAdapter = moshi.adapter(TagDto::class.java)

    @Test
    fun `legacy TagDto JSON deserializes into equivalent TagLocalEntity`() {
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

    @Test
    fun `TagLocalEntity serializes to byte-equivalent JSON as TagDto for every TagType`() {
        TagType.entries.forEach { type ->
            val name = "샘플-${type.name}"
            val entityJson = entityAdapter.toJson(TagLocalEntity(type.toLocalEntity(), name))
            val dtoJson = dtoAdapter.toJson(TagDto(type, name))
            assertEquals(dtoJson, entityJson)
        }
    }

    @Test
    fun `round-trip serialization preserves TagLocalEntity for every TagType`() {
        TagType.entries.forEach { type ->
            val original = TagLocalEntity(type.toLocalEntity(), "name-${type.name}")
            val parsed = entityAdapter.fromJson(entityAdapter.toJson(original))
            assertEquals(original, parsed)
        }
    }

    @Test
    fun `list serialization byte-equivalent between TagDto and TagLocalEntity`() {
        val entityListType = Types.newParameterizedType(List::class.java, TagLocalEntity::class.java)
        val dtoListType = Types.newParameterizedType(List::class.java, TagDto::class.java)
        val entityListAdapter = moshi.adapter<List<TagLocalEntity>>(entityListType)
        val dtoListAdapter = moshi.adapter<List<TagDto>>(dtoListType)

        val entities = listOf(
            TagLocalEntity(TagTypeLocalEntity.DEPARTMENT, "컴퓨터공학부"),
            TagLocalEntity(TagTypeLocalEntity.DEPARTMENT, "전기정보공학부"),
            TagLocalEntity(TagTypeLocalEntity.ETC, "영어진행 강의"),
        )
        val dtos = listOf(
            TagDto(TagType.DEPARTMENT, "컴퓨터공학부"),
            TagDto(TagType.DEPARTMENT, "전기정보공학부"),
            TagDto(TagType.ETC, "영어진행 강의"),
        )

        assertEquals(dtoListAdapter.toJson(dtos), entityListAdapter.toJson(entities))
    }
}
