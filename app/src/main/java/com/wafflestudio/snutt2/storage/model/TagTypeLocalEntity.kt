package com.wafflestudio.snutt2.storage.model

import com.wafflestudio.snutt2.domain.model.TagType

enum class TagTypeLocalEntity {
    SORT_CRITERIA,
    CLASSIFICATION,
    DEPARTMENT,
    ACADEMIC_YEAR,
    CREDIT,
    TIME,
    CATEGORY,
    CATEGORY_PRE2025,
    ETC,
}

fun TagTypeLocalEntity.toDomainModel(): TagType = when (this) {
    TagTypeLocalEntity.SORT_CRITERIA -> TagType.SORT_CRITERIA
    TagTypeLocalEntity.CLASSIFICATION -> TagType.CLASSIFICATION
    TagTypeLocalEntity.DEPARTMENT -> TagType.DEPARTMENT
    TagTypeLocalEntity.ACADEMIC_YEAR -> TagType.ACADEMIC_YEAR
    TagTypeLocalEntity.CREDIT -> TagType.CREDIT
    TagTypeLocalEntity.TIME -> TagType.TIME
    TagTypeLocalEntity.CATEGORY -> TagType.CATEGORY
    TagTypeLocalEntity.CATEGORY_PRE2025 -> TagType.CATEGORY_PRE2025
    TagTypeLocalEntity.ETC -> TagType.ETC
}

fun TagType.toLocalEntity(): TagTypeLocalEntity = when (this) {
    TagType.SORT_CRITERIA -> TagTypeLocalEntity.SORT_CRITERIA
    TagType.CLASSIFICATION -> TagTypeLocalEntity.CLASSIFICATION
    TagType.DEPARTMENT -> TagTypeLocalEntity.DEPARTMENT
    TagType.ACADEMIC_YEAR -> TagTypeLocalEntity.ACADEMIC_YEAR
    TagType.CREDIT -> TagTypeLocalEntity.CREDIT
    TagType.TIME -> TagTypeLocalEntity.TIME
    TagType.CATEGORY -> TagTypeLocalEntity.CATEGORY
    TagType.CATEGORY_PRE2025 -> TagTypeLocalEntity.CATEGORY_PRE2025
    TagType.ETC -> TagTypeLocalEntity.ETC
}
