package com.wafflestudio.snutt2.views.logged_in.home.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.friends.FriendRepository
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.FriendState
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<FriendUiEvent>()
    val uiEvent: SharedFlow<FriendUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            friendRepository.getFriends(FriendState.ACTIVE)
                .onSuccess { activeFriends ->
                    Timber.tag("aaaa").d("activeFriends")
                    Timber.tag("aaaa").d(activeFriends.toString())

                    friendRepository.getFriends(FriendState.REQUESTED)
                        .onSuccess { requestedFriends ->
                            Timber.tag("aaaa").d("requestedFriends")
                            Timber.tag("aaaa").d(requestedFriends.toString())

                            val selectedFriend = activeFriends.firstOrNull()
                            if (selectedFriend != null) {
                                friendRepository.getFriendCourseBooks(selectedFriend.id)
                                    .onSuccess { courseBooks ->
                                        Timber.tag("aaaa").d("courseBooks")
                                        Timber.tag("aaaa").d(courseBooks.toString())

                                        val selectedCourseBook = courseBooks.firstOrNull()
                                        if (selectedCourseBook != null) {
                                            friendRepository.getFriendPrimaryTable(
                                                selectedFriend.id,
                                                selectedCourseBook.year.toInt(),
                                                selectedCourseBook.semester.toInt(),
                                            )
                                                .onSuccess { table ->
                                                    _uiState.update {
                                                        FriendsUiState.Loaded(
                                                            activeFriends = activeFriends,
                                                            requestedFriends = requestedFriends,
                                                            selectedFriend = selectedFriend,
                                                            selectedFriendCourseBooks = courseBooks,
                                                            selectedCourseBook = selectedCourseBook,
                                                            selectedFriendTable = table,
                                                            selectedFriendTableTheme = when (table.themeRef) {
                                                                is ThemeReference.BuiltIn -> BuiltInTheme.fromCode(table.themeRef.code)
                                                                is ThemeReference.Custom -> BuiltInTheme.SNUTT
                                                            },
                                                            selectedFriendTableTrimParam = table.lectures.getFittingTrimParam(TableTrimParam.Default),
                                                            compactMode = tableDisplayRepository.compactMode.value,
                                                            tableLectureCustomOptions = tableDisplayRepository.tableLectureCustomOption.value,
                                                        )
                                                    }
                                                }
                                                .onFailure { error ->
                                                    _uiState.update { FriendsUiState.Error }
                                                    handleFriendError(error)
                                                }
                                        } else {
                                            _uiState.update {
                                                FriendsUiState.Loaded(
                                                    activeFriends = activeFriends,
                                                    requestedFriends = requestedFriends,
                                                    selectedFriend = selectedFriend,
                                                    selectedFriendCourseBooks = courseBooks,
                                                    selectedCourseBook = null,
                                                    selectedFriendTable = null,
                                                    compactMode = tableDisplayRepository.compactMode.value,
                                                    tableLectureCustomOptions = tableDisplayRepository.tableLectureCustomOption.value,
                                                    selectedFriendTableTrimParam = tableDisplayRepository.tableTrimParam.value,
                                                    selectedFriendTableTheme = BuiltInTheme.SNUTT,
                                                )
                                            }
                                        }
                                    }
                                    .onFailure { error ->
                                        _uiState.update { FriendsUiState.Error }
                                        handleFriendError(error)
                                    }
                            } else {
                                _uiState.update {
                                    FriendsUiState.Loaded(
                                        activeFriends = activeFriends,
                                        requestedFriends = requestedFriends,
                                        selectedFriend = null,
                                        selectedFriendCourseBooks = emptyList(),
                                        selectedCourseBook = null,
                                        selectedFriendTable = null,
                                        compactMode = tableDisplayRepository.compactMode.value,
                                        tableLectureCustomOptions = tableDisplayRepository.tableLectureCustomOption.value,
                                        selectedFriendTableTrimParam = tableDisplayRepository.tableTrimParam.value,
                                        selectedFriendTableTheme = BuiltInTheme.SNUTT,
                                    )
                                }
                            }
                        }
                        .onFailure { error ->
                            _uiState.update { FriendsUiState.Error }
                            handleFriendError(error)
                        }
                }
                .onFailure { error ->
                    Timber.tag("aaaa").d(error.toString())
                    _uiState.update { FriendsUiState.Error }
                    handleFriendError(error)
                }
        }
    }

    fun selectFriend(friend: Friend) {
        viewModelScope.launch {
            friendRepository.getFriendCourseBooks(friend.id)
                .onSuccess { courseBooks ->
                    val selectedCourseBook = courseBooks.firstOrNull()
                    if (selectedCourseBook != null) {
                        friendRepository.getFriendPrimaryTable(
                            friend.id,
                            selectedCourseBook.year.toInt(),
                            selectedCourseBook.semester.toInt(),
                        )
                            .onSuccess { table ->
                                _uiState.update { state ->
                                    when (state) {
                                        is FriendsUiState.Loaded -> state.copy(
                                            selectedFriend = friend,
                                            selectedFriendCourseBooks = courseBooks,
                                            selectedCourseBook = selectedCourseBook,
                                            selectedFriendTable = table,
                                            selectedFriendTableTheme = when (table.themeRef) {
                                                is ThemeReference.BuiltIn -> BuiltInTheme.fromCode(table.themeRef.code)
                                                is ThemeReference.Custom -> BuiltInTheme.SNUTT
                                            },
                                            selectedFriendTableTrimParam = table.lectures.getFittingTrimParam(TableTrimParam.Default),
                                        )

                                        else -> state
                                    }
                                }
                            }
                            .onFailure { error ->
                                handleFriendError(error)
                            }
                    } else {
                        _uiState.update { state ->
                            when (state) {
                                is FriendsUiState.Loaded -> state.copy(
                                    selectedFriend = friend,
                                    selectedFriendCourseBooks = courseBooks,
                                    selectedCourseBook = null,
                                    selectedFriendTable = null,
                                    selectedFriendTableTheme = BuiltInTheme.SNUTT,
                                    selectedFriendTableTrimParam = TableTrimParam.Default,
                                )

                                else -> state
                            }
                        }
                    }
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun selectCourseBook(courseBook: CourseBook) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is FriendsUiState.Loaded || currentState.selectedFriend == null) return@launch

            friendRepository.getFriendPrimaryTable(
                currentState.selectedFriend.id,
                courseBook.year.toInt(),
                courseBook.semester.toInt(),
            )
                .onSuccess { table ->
                    _uiState.update { state ->
                        when (state) {
                            is FriendsUiState.Loaded -> state.copy(
                                selectedCourseBook = courseBook,
                                selectedFriendTable = table,
                                selectedFriendTableTheme = when (table.themeRef) {
                                    is ThemeReference.BuiltIn -> BuiltInTheme.fromCode(table.themeRef.code)
                                    is ThemeReference.Custom -> BuiltInTheme.SNUTT
                                },
                                selectedFriendTableTrimParam = table.lectures.getFittingTrimParam(TableTrimParam.Default),
                            )

                            else -> state
                        }
                    }
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun openDrawer() {
        viewModelScope.launch {
            _uiEvent.emit(FriendUiEvent.OpenDrawer)
        }
    }

    fun closeDrawer() {
        viewModelScope.launch {
            _uiEvent.emit(FriendUiEvent.CloseDrawer)
        }
    }

    fun setDrawerTab(tab: FriendDrawerTab) {
        _uiState.update { state ->
            when (state) {
                is FriendsUiState.Loaded -> state.copy(drawerTab = tab)
                else -> state
            }
        }
    }

    fun openRequestFriendBottomSheet() {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is FriendsUiState.Loaded -> state.copy(bottomSheetContent = FriendBottomSheetContent.RequestMethodList)
                    else -> state
                }
            }
            _uiEvent.emit(FriendUiEvent.OpenBottomSheet)
        }
    }

    fun closeBottomSheet() {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is FriendsUiState.Loaded -> state.copy(bottomSheetContent = FriendBottomSheetContent.Hidden)
                    else -> state
                }
            }
            _uiEvent.emit(FriendUiEvent.CloseBottomSheet)
        }
    }

    fun showRequestWithNickname() {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is FriendsUiState.Loaded -> state.copy(bottomSheetContent = FriendBottomSheetContent.RequestWithNickname())
                    else -> state
                }
            }
            _uiEvent.emit(FriendUiEvent.OpenBottomSheet)
        }
    }

    fun requestFriend(nickname: String) {
        viewModelScope.launch {
            friendRepository.requestFriend(nickname)
                .onSuccess {
                    closeBottomSheet()
                    loadFriends() // Refresh
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun acceptFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.acceptFriend(friendId)
                .onSuccess {
                    loadFriends() // Refresh
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun declineFriend(friend: Friend) {
        viewModelScope.launch {
            friendRepository.declineFriend(friend.id)
                .onSuccess {
                    loadFriends() // Refresh
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun deleteFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.deleteFriend(friendId)
                .onSuccess {
                    loadFriends() // Refresh
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun openFriendDetailBottomSheet(friend: Friend) {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is FriendsUiState.Loaded -> state.copy(bottomSheetContent = FriendBottomSheetContent.FriendDetail(friend))
                    else -> state
                }
            }
            _uiEvent.emit(FriendUiEvent.OpenBottomSheet)
        }
    }

    fun openEditDisplayNameBottomSheet(friend: Friend) {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is FriendsUiState.Loaded -> state.copy(
                        bottomSheetContent = FriendBottomSheetContent.EditDisplayName(
                            friend,
                            friend.displayName ?: "",
                        ),
                    )

                    else -> state
                }
            }
            _uiEvent.emit(FriendUiEvent.OpenBottomSheet)
        }
    }

    fun saveDisplayName(friendId: String, displayName: String) {
        viewModelScope.launch {
            friendRepository.patchFriendDisplayName(friendId, displayName)
                .onSuccess {
                    closeBottomSheet()
                    loadFriends() // Refresh
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun dismissDialog() {
        _uiState.update { state ->
            when (state) {
                is FriendsUiState.Loaded -> state.copy(dialogState = FriendDialogState.None)
                else -> state
            }
        }
    }

    fun showDeleteFriendDialog(friend: Friend) {
        viewModelScope.launch {
            _uiState.update { state ->
                when (state) {
                    is FriendsUiState.Loaded -> state.copy(
                        dialogState = FriendDialogState.DeleteFriend(friend),
                        bottomSheetContent = FriendBottomSheetContent.Hidden,
                    )

                    else -> state
                }
            }
            _uiEvent.emit(FriendUiEvent.CloseBottomSheet)
        }
    }

    fun showDeclineFriendDialog(friend: Friend) {
        _uiState.update { state ->
            when (state) {
                is FriendsUiState.Loaded -> state.copy(dialogState = FriendDialogState.DeclineFriend(friend))
                else -> state
            }
        }
    }

    fun showGuideDialog() {
        _uiState.update { state ->
            when (state) {
                is FriendsUiState.Loaded -> state.copy(dialogState = FriendDialogState.ShowGuide)
                else -> state
            }
        }
    }

    fun confirmDeleteFriend(friend: Friend) {
        deleteFriend(friend.id)
        dismissDialog()
    }

    fun confirmDeclineFriend(friend: Friend) {
        declineFriend(friend)
        dismissDialog()
    }

    fun requestFriendWithKakaoTalk() {
        viewModelScope.launch {
            friendRepository.generateFriendLink()
                .onSuccess { requestToken ->
                    _uiEvent.emit(FriendUiEvent.ShareKakaoTalk(requestToken))
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    fun acceptFriendByKakaoLink(requestToken: String) {
        viewModelScope.launch {
            friendRepository.acceptFriendByLink(requestToken)
                .onSuccess { nickName ->
                    _uiEvent.emit(
                        FriendUiEvent.ShowAcceptFriendSuccess(nickName.getDisplayName()),
                    )
                    loadFriends() // Refresh friend list
                }
                .onFailure { error ->
                    handleFriendError(error)
                }
        }
    }

    private suspend fun handleFriendError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(FriendUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _uiEvent.emit(FriendUiEvent.LoggedOut)
            }

            else -> {
                _uiEvent.emit(FriendUiEvent.ShowToast(displayMessage))
            }
        }
    }
}
