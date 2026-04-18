package com.wafflestudio.snutt2.feature.diary.diarywrite

import com.wafflestudio.snutt2.domain.model.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domain.model.diary.DiaryQuestion
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.anySelected

sealed interface DiaryWriteUiState {
    data class Write(
        val lectureName: String,
        val dailyClassTypes: List<Selectable<DiaryDailyClassType>>,
        val activitySelectingState: ActivitySelectionState,
        val questions: List<DiaryQuestion>,
        val nextLecture: NextLecture? = null,
    ) : DiaryWriteUiState {

        fun allQuestionAnswered(): Boolean = questions.all { question -> question.selectableAnswers.anySelected() }
    }

    data object Error : DiaryWriteUiState
    data object Loading : DiaryWriteUiState
    data class Complete(
        val nextAction: DiaryNextAction,
    ) : DiaryWriteUiState
}

enum class ActivitySelectionState {
    InitialSelecting,
    Complete,
    ReSelecting,
    ;

    fun isSelecting() = this == InitialSelecting || this == ReSelecting
}

data class NextLecture(val lectureId: String, val courseTitle: String)

sealed interface DiaryNextAction {
    data object Nothing : DiaryNextAction
    data class WriteNext(val lectureId: String, val courseTitle: String) : DiaryNextAction
    data object WriteReview : DiaryNextAction
}
