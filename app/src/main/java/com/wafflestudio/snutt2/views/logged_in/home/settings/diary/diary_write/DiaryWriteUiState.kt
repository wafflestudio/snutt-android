package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import com.wafflestudio.snutt2.domainmodel.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestion
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.anySelected

sealed interface DiaryWriteUiState {
    data class Write(
        val lectureName: String,
        val dailyClassTypes: List<Selectable<DiaryDailyClassType>>,
        val activitySelectingState: ActivitySelectionState,
        val questions: List<DiaryQuestion>,
    ) : DiaryWriteUiState {

        fun allQuestionAnswered(): Boolean =
            questions.all { question -> question.selectableAnswers.anySelected() }

        fun copyWith(
            lectureName: String = this.lectureName,
            dailyClassTypes: List<Selectable<DiaryDailyClassType>> = this.dailyClassTypes,
            activitySelectingState: ActivitySelectionState = this.activitySelectingState,
            questions: List<DiaryQuestion> = this.questions,
        ): Write = this.copy(lectureName, dailyClassTypes, activitySelectingState, questions)
    }

    data object Error : DiaryWriteUiState
    data object Loading : DiaryWriteUiState
    data class Complete(
        val nextAction: DiaryNextAction,
    ) : DiaryWriteUiState
}

enum class ActivitySelectionState {
    InitialSelecting, Complete, ReSelecting;

    fun isSelecting() =
        this == InitialSelecting || this == ReSelecting
}

enum class DiaryNextAction {
    Nothing, WriteNext, WriteReview
}
