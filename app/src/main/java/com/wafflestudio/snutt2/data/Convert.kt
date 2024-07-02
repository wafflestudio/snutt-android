package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.core.network.model.CourseBookDto as CourseBookDtoNetwork
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto as CourseBookDtoDomain

fun CourseBookDtoNetwork.toExternalModel() = CourseBookDtoDomain(
    semester = semester,
    year = year,
)

fun List<CourseBookDtoNetwork>.toCourseBookExternalModel() = this.map { it.toExternalModel() }