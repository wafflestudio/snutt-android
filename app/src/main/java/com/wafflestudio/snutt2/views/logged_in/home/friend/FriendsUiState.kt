package com.wafflestudio.snutt2.views.logged_in.home.friend

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.TableTrimParam

sealed interface FriendsUiState {
    data object Loading : FriendsUiState

    data class Loaded(
        val activeFriends: List<Friend>,
        val requestedFriends: List<Friend>,
        val selectedFriend: Friend?,
        val selectedFriendCourseBooks: List<CourseBook>,
        val selectedCourseBook: CourseBook?,
        val selectedFriendTable: Table?,
        // FIXME: TimeTableNew 로직상, custom theme 일 때는 이 필드는 의미가 없음.
        val selectedFriendTableTheme: TableTheme,
        val selectedFriendTableTrimParam: TableTrimParam,
        val compactMode: Boolean = false,
        val tableLectureCustomOptions: TableLectureCustom = TableLectureCustom.Default,

        val drawerTab: FriendDrawerTab = FriendDrawerTab.ACTIVE,
        val bottomSheetContent: FriendBottomSheetContent = FriendBottomSheetContent.Hidden,
        val dialogState: FriendDialogState = FriendDialogState.None,
    ) : FriendsUiState

    data object Error : FriendsUiState
}

enum class FriendDrawerTab {
    ACTIVE,
    REQUESTED,
}

sealed interface FriendBottomSheetContent {
    data object Hidden : FriendBottomSheetContent
    data object RequestMethodList : FriendBottomSheetContent
    data class RequestWithNickname(val nickname: String = "") : FriendBottomSheetContent
    data class FriendDetail(val friend: Friend) : FriendBottomSheetContent
    data class EditDisplayName(val friend: Friend, val displayName: String) : FriendBottomSheetContent
}

sealed interface FriendDialogState {
    data object None : FriendDialogState
    data class DeleteFriend(val friend: Friend) : FriendDialogState
    data class DeclineFriend(val friend: Friend) : FriendDialogState
    data object ShowGuide : FriendDialogState
}

sealed interface FriendUiEvent {
    data object OpenDrawer : FriendUiEvent
    data object CloseDrawer : FriendUiEvent
    data object OpenBottomSheet : FriendUiEvent
    data object CloseBottomSheet : FriendUiEvent
    data class ShareKakaoTalk(val requestToken: String) : FriendUiEvent
    data class ShowAcceptFriendSuccess(val friendNickname: String) : FriendUiEvent
    data class ShowToast(val message: String) : FriendUiEvent
    data object LoggedOut : FriendUiEvent
}
