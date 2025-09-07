package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import com.wafflestudio.snutt2.domainmodel.diary.DiaryActivity
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestion
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.anySelected

sealed interface DiaryWriteUiState {
    abstract sealed class Write(
        open val lectureName: String,
        open val activities: List<Selectable<DiaryActivity>>,
        open val activitySelectingState: ActivitySelectionState,
        open val questions: List<DiaryQuestion>,
    ) : DiaryWriteUiState {

        data class New(
            override val lectureName: String,
            override val activities: List<Selectable<DiaryActivity>>,
            override val activitySelectingState: ActivitySelectionState,
            override val questions: List<DiaryQuestion>,
        ) : Write(lectureName, activities, activitySelectingState, questions)

        data class Edit(
            override val lectureName: String,
            override val activities: List<Selectable<DiaryActivity>>,
            override val activitySelectingState: ActivitySelectionState,
            override val questions: List<DiaryQuestion>,
        ) : Write(lectureName, activities, activitySelectingState, questions)

        fun allQuestionAnswered(): Boolean =
            questions.all { question -> question.selectableAnswers.anySelected() }

        fun copyWith(
            lectureName: String = this.lectureName,
            activities: List<Selectable<DiaryActivity>> = this.activities,
            activitySelectingState: ActivitySelectionState = this.activitySelectingState,
            questions: List<DiaryQuestion> = this.questions,
        ): Write = when (this) {
            is New -> this.copy(lectureName, activities, activitySelectingState, questions)
            is Edit -> this.copy(lectureName, activities, activitySelectingState, questions)
        }
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
