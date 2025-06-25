package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.DiaryWriteQuestion

val diaryWriteTodayOptions = listOf("개강", "수업", "실기", "시험", "발표", "휴강", "종강", "드랍")

fun diaryWriteQuestionList(lectureName: String, today: List<String>): List<DiaryWriteQuestion> {
    return when {
        today.contains("개강") -> listOf(
            DiaryWriteQuestion("수강신청은 쉬웠나요?", listOf("널널했어요", "2~3픽 했어요", "1픽 했어요", "초안지 썼어요")),
            DiaryWriteQuestion("교수님/수업 첫인상은 어땠나요?", listOf("즐거울 것 같아요", "별 생각 없어요", "큰일 난 것 같아요")),
            DiaryWriteQuestion("수업 끝까지 들을 것 같나요?", listOf("끝까지 들을 거에요", "모르겠어요", "드랍할 것 같아요")),
        )

        today.contains("시험") -> listOf(
            DiaryWriteQuestion("시험 잘 보셨나요?", listOf("잘 본 것 같아요", "공부한만큼 풀었어요", "조용히 하세요")),
            DiaryWriteQuestion("무슨 시험이었나요?", listOf("중간", "기말", "1차", "2차", "3차", "쪽지시험", "수시")),
            DiaryWriteQuestion("어떤 시험이었나요?", listOf("오픈북이었어요", "서술형이었어요", "등등")),
        )

        today.contains("드랍") -> listOf(
            DiaryWriteQuestion("왜 드랍하셨나요?", listOf("전공수업 들어야 해서요", "흥미가 없어서요", "시험 망해서요")),
            DiaryWriteQuestion("다시 들을 건가요?", listOf("네", "고민 중이에요", "아니요")),
            DiaryWriteQuestion("친구들도 $lectureName 수업을 드랍했나요?", listOf("아니요", "모르겠어요", "네")),
        )

        today.contains("종강") -> listOf(
            DiaryWriteQuestion("${lectureName}은 어떤 수업인가요?", listOf("명강이에요\uD83D\uDC4D", "꿀강이에요\uD83C\uDF6F", "고진감래 ⛰\uFE0F", "무난해요 \uD83D\uDE00")),
            DiaryWriteQuestion("다시 들을 건가요?", listOf("네", "고민 중이에요", "아니요")),
            DiaryWriteQuestion("친구들도 $lectureName 수업을 드랍했나요?", listOf("아니요", "모르겠어요", "네")),
        )

        today.contains("휴강") -> listOf(
            DiaryWriteQuestion("왜 휴강했나요?", listOf("자체휴강", "모르겠어요", "휴일", "교수님 개인사정")),
            DiaryWriteQuestion("지금 과제가 많나요?", listOf("없어요", "할만 했어요", "쌓여 있어요")),
            DiaryWriteQuestion("다음 수업시간에는 무엇을 하나요?", listOf("수업", "시험", "실기", "발표")),
        )

        else -> listOf(
            DiaryWriteQuestion("오늘 재미있었나요?", listOf("흥미진진했어요", "별 생각 없어요", "시간 낭비였어요")),
            DiaryWriteQuestion("지금 과제가 많나요?", listOf("없어요", "할만 했어요", "쌓여 있어요")),
            DiaryWriteQuestion("다음 수업시간에는 무엇을 하나요?", listOf("수업", "시험", "실기", "발표")),
        )
    }
}

sealed interface DiaryWriteUiState {
    data class Success(val diaryWrite: DiaryWrite) : DiaryWriteUiState
    data object Error : DiaryWriteUiState
    data object Loading : DiaryWriteUiState
    data object Empty : DiaryWriteUiState
}
