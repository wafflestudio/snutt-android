package com.wafflestudio.snutt2.views.logged_in.home.friend

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.Table

sealed interface FriendsUiState {
    data object Loading : FriendsUiState

    data class Loaded(
        val activeFriends: List<Friend>,
        val requestedFriends: List<Friend>,
        val selectedFriend: Friend?,
        val selectedFriendCourseBooks: List<CourseBook>,
        val selectedCourseBook: CourseBook?,
        val selectedFriendTable: Table?,
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
