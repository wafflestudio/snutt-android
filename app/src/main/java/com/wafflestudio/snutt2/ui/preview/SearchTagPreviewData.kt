package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.TagType

object SearchTagPreviewData {
    val previewTagTypes = listOf(
        TagType.SORT_CRITERIA,
        TagType.CLASSIFICATION,
        TagType.DEPARTMENT,
        TagType.ACADEMIC_YEAR,
        TagType.CREDIT,
        TagType.TIME,
        TagType.ETC,
    )

    val previewAllTags = listOf(
        SearchTag.Regular(TagType.SORT_CRITERIA, "평점 높은 순"),
        SearchTag.Regular(TagType.SORT_CRITERIA, "강의평 많은 순"),
        SearchTag.Regular(TagType.CLASSIFICATION, "공통"),
        SearchTag.Regular(TagType.CLASSIFICATION, "교양"),
        SearchTag.Regular(TagType.CLASSIFICATION, "논문"),
        SearchTag.Regular(TagType.CLASSIFICATION, "일선"),
        SearchTag.Regular(TagType.CLASSIFICATION, "전선"),
        SearchTag.Regular(TagType.CLASSIFICATION, "전필"),
        SearchTag.Regular(TagType.DEPARTMENT, "컴퓨터공학부"),
        SearchTag.Regular(TagType.DEPARTMENT, "전기정보공학부"),
        SearchTag.Regular(TagType.DEPARTMENT, "기계공학부"),
        SearchTag.Regular(TagType.ACADEMIC_YEAR, "1학년"),
        SearchTag.Regular(TagType.ACADEMIC_YEAR, "2학년"),
        SearchTag.Regular(TagType.ACADEMIC_YEAR, "3학년"),
        SearchTag.Regular(TagType.CREDIT, "1학점"),
        SearchTag.Regular(TagType.CREDIT, "2학점"),
        SearchTag.Regular(TagType.CREDIT, "3학점"),
        SearchTag.TimeEmpty,
        SearchTag.TimeSelect,
        SearchTag.EtcEng,
        SearchTag.EtcMilitary,
    )
}
