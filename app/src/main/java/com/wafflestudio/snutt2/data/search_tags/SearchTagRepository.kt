package com.wafflestudio.snutt2.data.search_tags

import com.wafflestudio.snutt2.model.TagDto

interface SearchTagRepository {
    suspend fun getTags(year: Long, semester: Long): List<TagDto>
}
