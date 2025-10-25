package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.diary.DiaryActivity
import com.wafflestudio.snutt2.domainmodel.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.lib.network.Result

// TODO: Diary 관련해서는 data layer 다 한 repository 로 통일하기
interface DiaryRepository {
    suspend fun saveDiaryWrite(
        lectureId: String,
        activities: List<DiaryActivity>,
        questionAnswers: List<DiaryAnsweredQuestion>,
        comment: String,
    ): Result<Unit>
}
