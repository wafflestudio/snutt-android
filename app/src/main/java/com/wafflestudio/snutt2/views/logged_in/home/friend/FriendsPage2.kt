package com.wafflestudio.snutt2.views.logged_in.home.friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddFriendIcon
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.DrawerIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.PersonIcon
import com.wafflestudio.snutt2.components.compose.QuestionCircleIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import kotlinx.coroutines.launch

@Composable
fun FriendsRoute(
    viewModel: FriendsViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    // 카카오톡 링크로 진입한 경우 처리
    LaunchedEffect(Unit) {
        val activity = context as? android.app.Activity
        val requestToken = activity?.intent?.data?.getQueryParameter("requestToken")
        val type = activity?.intent?.data?.getQueryParameter("type")

        if (requestToken != null && type == "add-friend-kakao") {
            viewModel.acceptFriendByKakaoLink(requestToken)
            viewModel.openDrawer()
            // intent data를 null로 설정해서 다시 처리되지 않도록 함
            activity?.intent?.data = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FriendUiEvent.OpenDrawer -> {
                    scope.launch {
                        drawerState.open()
                    }
                }

                is FriendUiEvent.CloseDrawer -> {
                    scope.launch {
                        drawerState.close()
                    }
                }

                is FriendUiEvent.OpenBottomSheet -> {
                    scope.launch {
                        bottomSheetState.show()
                    }
                }

                is FriendUiEvent.CloseBottomSheet -> {
                    scope.launch {
                        bottomSheetState.hide()
                    }
                }

                is FriendUiEvent.ShareKakaoTalk -> {
                    val feedTemplate = com.wafflestudio.snutt2.kakao.buildKakaoAddFriendTemplate(
                        context,
                        mapOf(
                            "requestToken" to event.requestToken,
                            "type" to "add-friend-kakao",
                        ),
                    )
                    com.wafflestudio.snutt2.kakao.sendKakaoMessageWithTemplate(
                        context,
                        feedTemplate,
                    )
                }

                is FriendUiEvent.ShowAcceptFriendSuccess -> {
                    context.toast(
                        context.getString(
                            com.wafflestudio.snutt2.R.string.kakao_friend_accept_success,
                            event.friendNickname,
                        ),
                    )
                }

                is FriendUiEvent.ShowToast -> {
                    context.toast(event.message)
                }

                is FriendUiEvent.LoggedOut -> {
                    // TODO: 로그아웃 처리
                }
            }
        }
    }

    FriendsScreen(
        uiState = uiState,
        bottomBar = bottomBar,
        drawerState = drawerState,
        bottomSheetState = bottomSheetState,
        onSelectFriend = viewModel::selectFriend,
        onSelectCourseBook = viewModel::selectCourseBook,
        onOpenDrawer = viewModel::openDrawer,
        onCloseDrawer = viewModel::closeDrawer,
        onSetDrawerTab = viewModel::setDrawerTab,
        onOpenRequestFriendBottomSheet = viewModel::openRequestFriendBottomSheet,
        onCloseBottomSheet = viewModel::closeBottomSheet,
        onAcceptFriend = viewModel::acceptFriend,
        onDeclineFriend = viewModel::showDeclineFriendDialog,
        onOpenFriendDetail = viewModel::openFriendDetailBottomSheet,
        onRequestWithNickname = viewModel::showRequestWithNickname,
        onRequestWithKakaoTalk = viewModel::requestFriendWithKakaoTalk,
        onSubmitNickname = viewModel::requestFriend,
        onDeleteFriend = viewModel::showDeleteFriendDialog,
        onEditDisplayName = viewModel::openEditDisplayNameBottomSheet,
        onSubmitDisplayName = { friend, displayName ->
            viewModel.saveDisplayName(
                friend.id,
                displayName,
            )
        },
        onDismissDialog = viewModel::dismissDialog,
        onConfirmDeleteFriend = viewModel::confirmDeleteFriend,
        onConfirmDeclineFriend = viewModel::confirmDeclineFriend,
        onShowGuideDialog = viewModel::showGuideDialog,
    )
}

@Composable
fun FriendsScreen(
    uiState: FriendsUiState,
    bottomBar: @Composable () -> Unit,
    drawerState: androidx.compose.material.DrawerState,
    bottomSheetState: androidx.compose.material.ModalBottomSheetState,
    onSelectFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onSelectCourseBook: (com.wafflestudio.snutt2.domainmodel.CourseBook) -> Unit,
    onOpenDrawer: () -> Unit,
    onCloseDrawer: () -> Unit,
    onSetDrawerTab: (FriendDrawerTab) -> Unit,
    onOpenRequestFriendBottomSheet: () -> Unit,
    onCloseBottomSheet: () -> Unit,
    onAcceptFriend: (String) -> Unit,
    onDeclineFriend: (Friend) -> Unit,
    onOpenFriendDetail: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onRequestWithNickname: () -> Unit,
    onRequestWithKakaoTalk: () -> Unit,
    onSubmitNickname: (String) -> Unit,
    onDeleteFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onEditDisplayName: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onSubmitDisplayName: (com.wafflestudio.snutt2.domainmodel.Friend, String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onConfirmDeclineFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onShowGuideDialog: () -> Unit,
) {
    when (uiState) {
        is FriendsUiState.Loading -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                bottomBar()
            }
        }

        is FriendsUiState.Loaded -> {
            FriendsLoadedScreen(
                uiState = uiState,
                bottomBar = bottomBar,
                drawerState = drawerState,
                bottomSheetState = bottomSheetState,
                onSelectFriend = onSelectFriend,
                onSelectCourseBook = onSelectCourseBook,
                onOpenDrawer = onOpenDrawer,
                onCloseDrawer = onCloseDrawer,
                onSetDrawerTab = onSetDrawerTab,
                onOpenRequestFriendBottomSheet = onOpenRequestFriendBottomSheet,
                onCloseBottomSheet = onCloseBottomSheet,
                onAcceptFriend = onAcceptFriend,
                onDeclineFriend = onDeclineFriend,
                onOpenFriendDetail = onOpenFriendDetail,
                onRequestWithNickname = onRequestWithNickname,
                onRequestWithKakaoTalk = onRequestWithKakaoTalk,
                onSubmitNickname = onSubmitNickname,
                onDeleteFriend = onDeleteFriend,
                onEditDisplayName = onEditDisplayName,
                onSubmitDisplayName = onSubmitDisplayName,
                onDismissDialog = onDismissDialog,
                onConfirmDeleteFriend = onConfirmDeleteFriend,
                onConfirmDeclineFriend = onConfirmDeclineFriend,
                onShowGuideDialog = onShowGuideDialog,
            )
        }

        is FriendsUiState.Error -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.friend_load_error),
                        style = SNUTTTypography.body1,
                    )
                }
                bottomBar()
            }
        }
    }
}

@Composable
private fun FriendsLoadedScreen(
    uiState: FriendsUiState.Loaded,
    bottomBar: @Composable () -> Unit,
    drawerState: androidx.compose.material.DrawerState,
    bottomSheetState: androidx.compose.material.ModalBottomSheetState,
    onSelectFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onSelectCourseBook: (com.wafflestudio.snutt2.domainmodel.CourseBook) -> Unit,
    onOpenDrawer: () -> Unit,
    onCloseDrawer: () -> Unit,
    onSetDrawerTab: (FriendDrawerTab) -> Unit,
    onOpenRequestFriendBottomSheet: () -> Unit,
    onCloseBottomSheet: () -> Unit,
    onAcceptFriend: (String) -> Unit,
    onDeclineFriend: (Friend) -> Unit,
    onOpenFriendDetail: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onRequestWithNickname: () -> Unit,
    onRequestWithKakaoTalk: () -> Unit,
    onSubmitNickname: (String) -> Unit,
    onDeleteFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onEditDisplayName: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onSubmitDisplayName: (com.wafflestudio.snutt2.domainmodel.Friend, String) -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDeleteFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onConfirmDeclineFriend: (com.wafflestudio.snutt2.domainmodel.Friend) -> Unit,
    onShowGuideDialog: () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            FriendsBottomSheetContent(
                bottomSheetContent = uiState.bottomSheetContent,
                onDismiss = onCloseBottomSheet,
                onRequestWithNickname = onRequestWithNickname,
                onRequestWithKakaoTalk = onRequestWithKakaoTalk,
                onSubmitNickname = onSubmitNickname,
                onDeleteFriend = onDeleteFriend,
                onEditDisplayName = onEditDisplayName,
                onSubmitDisplayName = onSubmitDisplayName,
            )
        },
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
    ) {
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                FriendsDrawerContent(
                    uiState = uiState,
                    onClose = {
                        onCloseDrawer()
                    },
                    onSelectTab = onSetDrawerTab,
                    onSelectFriend = { friend ->
                        onSelectFriend(friend)
                        onCloseDrawer()
                    },
                    onOpenAddFriendBottomSheet = onOpenRequestFriendBottomSheet,
                    onOpenFriendDetail = onOpenFriendDetail,
                    onAcceptFriend = onAcceptFriend,
                    onDeclineFriend = onDeclineFriend,
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SNUTTColors.White900),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconWithAlertDot(uiState.requestedFriends.isNotEmpty()) { centerAlignedModifier ->
                            DrawerIcon(
                                modifier = centerAlignedModifier
                                    .size(30.dp)
                                    .clicks { onOpenDrawer() },
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                stringResource(R.string.friend_timetable_title),
                                style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            QuestionCircleIcon(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clicks {
                                        onShowGuideDialog()
                                    },
                            )
                        }
                        AddFriendIcon(
                            modifier = Modifier
                                .size(25.dp)
                                .clicks { onOpenRequestFriendBottomSheet() },
                            colorFilter = ColorFilter.tint(SNUTTColors.Gray600),
                        )
                    }

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = SNUTTColors.Gray200,
                        thickness = 0.5.dp,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 36.dp, end = 36.dp, top = 20.dp, bottom = 15.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (uiState.selectedFriend != null) {
                            Row(
                                modifier = Modifier.weight(1f, fill = false),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                PersonIcon(
                                    modifier = Modifier.size(15.dp),
                                    colorFilter = ColorFilter.tint(SNUTTColors.SNUTTTheme),
                                )
                                Spacer(modifier = Modifier.size(2.dp))
                                Text(
                                    text = uiState.selectedFriend.let {
                                        it.displayName ?: "${it.nickname.nickname}#${it.nickname.tag}"
                                    },
                                    style = SNUTTTypography.body1.copy(color = SNUTTColors.SNUTTTheme),
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            SemesterDropdown(
                                courseBooks = uiState.selectedFriendCourseBooks,
                                selectedCourseBook = uiState.selectedCourseBook,
                                onSelectCourseBook = onSelectCourseBook,
                            )
                        }
                    }

                    // Friend Timetable
                    val friendTable = uiState.selectedFriendTable
                    if (uiState.selectedFriend != null && friendTable != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SNUTTColors.White900),
                        ) {
                            TimeTable(
                                lectures = friendTable.lectures,
                                selectedLecture = null,
                                fittedTrimParam = uiState.selectedFriendTableTrimParam,
                                theme = uiState.selectedFriendTableTheme,
                                isDarkMode = isDarkMode(),
                                compactMode = uiState.compactMode,
                                tableLectureCustomOptions = uiState.tableLectureCustomOptions,
                                touchEnabled = false, // 친구 시간표는 일단 터치 불가
                            )
                        }
                    } else if (uiState.selectedFriend != null && uiState.selectedFriendCourseBooks.isEmpty()) {
                        // 친구는 있지만 시간표가 없는 경우
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.friend_timetable_empty),
                                style = SNUTTTypography.body1,
                                color = SNUTTColors.Gray600,
                            )
                        }
                    } else {
                        // 친구가 없는 경우
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 169.5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                modifier = Modifier.size(width = 50.dp, height = 58.dp),
                                painter = painterResource(id = R.drawable.ic_cat_retry),
                                contentDescription = stringResource(R.string.friend_list_empty),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.friend_list_empty),
                                style = SNUTTTypography.subtitle1,
                                color = SNUTTColors.Black900,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.friend_empty_guide_action),
                                    style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
                                    color = SNUTTColors.Gray600,
                                )
                                AddFriendIcon(
                                    modifier = Modifier.size(12.5.dp),
                                    colorFilter = ColorFilter.tint(SNUTTColors.Gray600),
                                )
                                Text(
                                    text = stringResource(R.string.friend_empty_guide_action2),
                                    style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
                                    color = SNUTTColors.Gray600,
                                )
                            }
                            Text(
                                text = stringResource(R.string.friend_empty_guide_message),
                                style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
                                color = SNUTTColors.TextMed,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(R.string.friend_empty_guide_sidebar),
                                    style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
                                    color = SNUTTColors.TextMed,
                                )
                                DrawerIcon(
                                    modifier = Modifier.size(12.5.dp),
                                    colorFilter = ColorFilter.tint(SNUTTColors.TextMed),
                                )
                                Text(
                                    text = stringResource(R.string.friend_empty_guide_sidebar2),
                                    style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
                                    color = SNUTTColors.TextMed,
                                )
                            }
                            Spacer(modifier = Modifier.height(19.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clicks {
                                    onShowGuideDialog()
                                },
                            ) {
                                QuestionCircleIcon(modifier = Modifier.size(11.5.dp))
                                Text(
                                    text = stringResource(R.string.friend_guide_detail),
                                    style = SNUTTTypography.subtitle2.copy(fontSize = 11.sp),
                                    color = SNUTTColors.Gray600,
                                    textDecoration = TextDecoration.Underline,
                                )
                            }
                        }
                    }
                }
                bottomBar()
            }
        }
    }

    // Dialog
    FriendsDialogContent(
        dialogState = uiState.dialogState,
        onDismiss = onDismissDialog,
        onConfirmDeleteFriend = onConfirmDeleteFriend,
        onConfirmDeclineFriend = onConfirmDeclineFriend,
    )
}

@Composable
private fun SemesterDropdown(
    courseBooks: List<com.wafflestudio.snutt2.domainmodel.CourseBook>,
    selectedCourseBook: com.wafflestudio.snutt2.domainmodel.CourseBook?,
    onSelectCourseBook: (com.wafflestudio.snutt2.domainmodel.CourseBook) -> Unit,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = SNUTTColors.Gray200,
                    shape = RoundedCornerShape(4.dp),
                )
                .clicks { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedCourseBook?.toFormattedString(context) ?: "",
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black900),
            )
            Spacer(modifier = Modifier.width(5.dp))
            ArrowDownIcon(modifier = Modifier.size(15.dp))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            courseBooks.forEach { courseBook ->
                DropdownMenuItem(
                    onClick = {
                        onSelectCourseBook(courseBook)
                        expanded = false
                    },
                ) {
                    Text(
                        text = courseBook.toFormattedString(context),
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.Black900),
                    )
                }
            }
        }
    }
}
