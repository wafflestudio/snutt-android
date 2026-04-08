package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.network.dto.CourseBookDto

fun CourseBookDto.toDomain(): CourseBook = CourseBook(
    semester = semester,
    year = year,
)
