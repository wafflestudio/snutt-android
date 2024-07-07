package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.CourseBook
import com.wafflestudio.snutt2.core.network.model.CourseBookDto

fun CourseBookDto.toExternalModel() = CourseBook(
    semester = this.semester,
    year = this.year,
)