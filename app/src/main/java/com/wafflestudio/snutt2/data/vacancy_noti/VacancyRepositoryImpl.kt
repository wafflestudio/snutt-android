package com.wafflestudio.snutt2.data.vacancy_noti

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VacancyRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi
) : VacancyRepository {

    override fun getVacancyLectures(): List<LectureDto> {
        return List(10) {
            LectureDto(
                id = "",
                academic_year = "1학년",
                category = "사고와 표현",
                class_time_json = listOf(
                    ClassTimeDto(
                        day = 1,
                        place = "43-1-403",
                        start_time = "11:00",
                        end_time = "12:15",
                        len = 1.5f,
                        start = 3.0f
                    ),
                    ClassTimeDto(
                        day = 3,
                        place = "43-1-403",
                        start_time = "11:00",
                        end_time = "12:15",
                        len = 1.5f,
                        start = 3.0f
                    )
                ),
                classification = "교양",
                credit = 3,
                department = "기초교육원",
                instructor = "현영종",
                lecture_number = "005",
                quota = 25,
                freshmanQuota = 13,
                remark = "",
                course_number = "031.031",
                course_title = "말하기와 토론",
                class_time_mask = emptyList(),
                registrationCount = 10,
            ).copy(
                id = it.toString(),
                registrationCount = (24L..25L).random()
            )
        }
    }
}
