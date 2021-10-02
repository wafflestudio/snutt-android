package com.wafflestudio.snutt2.data.search_tags

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchTagRepositoryImpl @Inject constructor(private val api: SNUTTRestApi) : SearchTagRepository {

    override suspend fun getTags(year: Long, semester: Long): List<TagDto> {
        val response = api._getTagList(year.toInt(), semester.toInt())
        val list = mutableListOf<TagDto>()
        list.apply {
            addAll(response.department.map { TagDto(TagType.DEPARTMENT, it) })
            addAll(response.classification.map { TagDto(TagType.CLASSIFICATION, it) })
            addAll(response.academicYear.map { TagDto(TagType.ACADEMIC_YEAR, it) })
            addAll(response.credit.map { TagDto(TagType.CREDIT, it) })
            addAll(response.instructor.map { TagDto(TagType.INSTRUCTOR, it) })
            addAll(response.category.map { TagDto(TagType.CATEGORY, it) })
        }
        return list
    }
}
