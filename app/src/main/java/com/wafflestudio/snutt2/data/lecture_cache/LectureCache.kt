package com.wafflestudio.snutt2.data.lecture_cache

import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureCache @Inject constructor() {
    // TODO: Lecture 의 network model 말고 data model 따로 만들기
    private val cache = ConcurrentHashMap<String, LectureDto>()

    fun put(lecture: LectureDto) {
        cache[lecture.id] = lecture
    }

    fun putAll(lectures: List<LectureDto>) {
        lectures.forEach { put(it) }
    }

    fun get(id: String): LectureDto? = cache[id]
    fun clear() {
        cache.clear()
    }
}
