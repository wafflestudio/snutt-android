package com.wafflestudio.snutt2.feature.friend

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.ui.components.compose.RedDot
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.FriendPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.preview.TableSummaryPreviewData
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun FriendsDrawerContent(
    modifier: Modifier = Modifier,
    uiState: FriendsUiState.Loaded,
    onClose: () -> Unit,
    onSelectTab: (FriendDrawerTab) -> Unit,
    onSelectFriend: (Friend) -> Unit,
    onOpenAddFriendBottomSheet: () -> Unit,
    onOpenFriendDetail: (Friend) -> Unit,
    onAcceptFriend: (Friend) -> Unit,
    onDeclineFriend: (Friend) -> Unit,
) {
    Column(
        modifier = modifier
            .background(SNUTTColors.White900)
            .fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.5.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SnuttIcon(R.drawable.logo, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(11.dp))
            Text(
                text = stringResource(R.string.sign_in_logo_title),
                style = SNUTTTypography.h2,
            )
            Spacer(modifier = Modifier.weight(1f))
            SnuttIcon(
                R.drawable.ic_exit,
                modifier = Modifier
                    .size(30.dp)
                    .clicks {
                        onClose()
                    },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }

        Divider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = SNUTTColors.Gray100,
            thickness = 1.dp,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clicks { onSelectTab(FriendDrawerTab.ACTIVE) }
                    .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.friend_list_tab),
                    style = SNUTTTypography.button.copy(
                        color = if (uiState.drawerTab == FriendDrawerTab.ACTIVE) SNUTTColors.Black900 else SNUTTColors.Gray200,
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    color = if (uiState.drawerTab == FriendDrawerTab.ACTIVE) SNUTTColors.Gray200 else SNUTTColors.Gray100,
                    thickness = 3.dp,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clicks { onSelectTab(FriendDrawerTab.REQUESTED) }
                    .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.friend_request_tab),
                        style = SNUTTTypography.button.copy(
                            color = if (uiState.drawerTab == FriendDrawerTab.REQUESTED) SNUTTColors.Black900 else SNUTTColors.Gray200,
                        ),
                    )
                    if (uiState.requestedFriends.isNotEmpty()) {
                        Spacer(modifier = Modifier.size(1.dp))
                        RedDot()
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    color = if (uiState.drawerTab == FriendDrawerTab.REQUESTED) SNUTTColors.Gray200 else SNUTTColors.Gray100,
                    thickness = 3.dp,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, top = 19.dp, bottom = 9.dp)
                .clicks { onOpenAddFriendBottomSheet() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.friend_add_action),
                style = SNUTTTypography.body2,
                color = SNUTTColors.Gray600,
                modifier = Modifier.weight(1f),
            )
            SnuttIcon(
                R.drawable.ic_user_add,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Gray600),
            )
        }

        Divider(
            color = SNUTTColors.Gray100,
            thickness = 1.5.dp,
            modifier = Modifier.padding(horizontal = 40.dp),
        )

        when (uiState.drawerTab) {
            FriendDrawerTab.ACTIVE -> {
                FriendsActiveList(
                    friends = uiState.activeFriends,
                    selectedFriendId = uiState.selectedFriend?.id,
                    onSelectFriend = onSelectFriend,
                    onOpenFriendDetail = onOpenFriendDetail,
                )
            }

            FriendDrawerTab.REQUESTED -> {
                FriendsRequestedList(
                    friends = uiState.requestedFriends,
                    onAcceptFriend = onAcceptFriend,
                    onDeclineFriend = onDeclineFriend,
                )
            }
        }
    }
}

@Composable
private fun FriendsActiveList(
    friends: List<Friend>,
    selectedFriendId: String?,
    onSelectFriend: (Friend) -> Unit,
    onOpenFriendDetail: (Friend) -> Unit,
) {
    if (friends.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 138.5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(width = 50.dp, height = 58.dp),
                painter = painterResource(id = R.drawable.ic_cat_retry),
                contentDescription = stringResource(R.string.friend_list_empty),
            )
            Spacer(modifier = Modifier.height(21.dp))
            Text(
                text = stringResource(R.string.friend_list_empty),
                style = SNUTTTypography.subtitle2,
                color = SNUTTColors.Black900,
            )
            Spacer(modifier = Modifier.height(9.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            ) {
                Text(
                    text = stringResource(R.string.friend_add_action),
                    style = SNUTTTypography.subtitle2.copy(fontSize = 11.sp),
                    color = SNUTTColors.Gray600,
                )
                SnuttIcon(
                    R.drawable.ic_user_add,
                    modifier = Modifier.size(11.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Gray600),
                )
                Text(
                    text = stringResource(R.string.friend_list_empty_guide1),
                    style = SNUTTTypography.subtitle2.copy(fontSize = 11.sp),
                    color = SNUTTColors.TextMed,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.friend_list_empty_guide2),
                style = SNUTTTypography.subtitle2.copy(fontSize = 11.sp),
                color = SNUTTColors.TextMed,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(friends, key = { it.id }) { friend ->
                FriendListItem(
                    friend = friend,
                    isSelected = friend.id == selectedFriendId,
                    onClick = { onSelectFriend(friend) },
                    onMoreClick = { onOpenFriendDetail(friend) },
                )
            }
        }
    }
}

@Composable
private fun FriendsRequestedList(
    friends: List<Friend>,
    onAcceptFriend: (Friend) -> Unit,
    onDeclineFriend: (Friend) -> Unit,
) {
    if (friends.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 138.5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(width = 50.dp, height = 58.dp),
                painter = painterResource(id = R.drawable.ic_cat_retry),
                contentDescription = stringResource(R.string.friend_request_empty),
            )
            Spacer(modifier = Modifier.height(21.dp))
            Text(
                text = stringResource(R.string.friend_request_empty),
                style = SNUTTTypography.subtitle2,
                color = SNUTTColors.Black900,
            )
            Spacer(modifier = Modifier.height(9.dp))
            Text(
                text = stringResource(R.string.friend_request_empty_guide1),
                style = SNUTTTypography.subtitle2.copy(fontSize = 11.sp),
                color = SNUTTColors.TextMed,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.friend_request_empty_guide2),
                style = SNUTTTypography.subtitle2.copy(fontSize = 11.sp),
                color = SNUTTColors.TextMed,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(friends, key = { it.id }) { friend ->
                FriendRequestItem(
                    friend = friend,
                    onAccept = { onAcceptFriend(friend) },
                    onDecline = { onDeclineFriend(friend) },
                )
            }
        }
    }
}

@Composable
private fun FriendListItem(
    friend: Friend,
    isSelected: Boolean,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) SNUTTColors.Gray100 else Color.Transparent)
            .clicks { onClick() }
            .padding(start = 36.dp, end = 20.dp, top = 13.dp, bottom = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = friend.displayName ?: "${friend.nickname.nickname}#${friend.nickname.tag}",
            style = SNUTTTypography.body1,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        SnuttIcon(
            R.drawable.ic_more,
            modifier = Modifier
                .size(30.dp)
                .clicks { onMoreClick() },
            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
        )
    }
}

@Composable
private fun FriendRequestItem(
    friend: Friend,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, end = 28.5.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${friend.nickname.nickname}#${friend.nickname.tag}",
            style = SNUTTTypography.body1,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = SNUTTColors.Gray200,
                    shape = RoundedCornerShape(corner = CornerSize(3.dp)),
                )
                .clicks {
                    onDecline()
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(
                stringResource(R.string.friend_decline),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Gray200),
            )
        }
        Divider(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .border(
                    width = 0.5.dp,
                    color = SNUTTColors.MainBlue,
                    shape = RoundedCornerShape(corner = CornerSize(3.dp)),
                )
                .clicks {
                    onAccept()
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(
                stringResource(R.string.friend_accept),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.MainBlue),
            )
        }
    }
}

private val sampleFriendsUiStateLoaded = FriendsUiState.Loaded(
    activeFriends = FriendPreviewData.sampleFriends,
    requestedFriends = FriendPreviewData.sampleRequestedFriends,
    selectedFriend = FriendPreviewData.sampleFriends.firstOrNull(),
    selectedFriendCourseBooks = TableSummaryPreviewData.sampleCourseBooks,
    selectedCourseBook = TableSummaryPreviewData.sampleCourseBooks.firstOrNull(),
    selectedFriendTable = FriendPreviewData.sampleFriendTable,
    selectedFriendTableTheme = BuiltInTheme.SNUTT,
    selectedFriendTableTrimParam = TableTrimParam.Default,
    drawerTab = FriendDrawerTab.ACTIVE,
    bottomSheetContent = FriendBottomSheetContent.Hidden,
    dialogState = FriendDialogState.None,
)

@SnuttPreview
@Composable
private fun FriendsDrawer_FriendsTab() {
    SnuttPreviewSurface {
        FriendsDrawerContent(
            uiState = sampleFriendsUiStateLoaded,
            onClose = {},
            onSelectTab = {},
            onSelectFriend = {},
            onOpenAddFriendBottomSheet = {},
            onOpenFriendDetail = {},
            onAcceptFriend = {},
            onDeclineFriend = {},
        )
    }
}

@SnuttPreview
@Composable
private fun FriendsDrawer_RequestsTab() {
    SnuttPreviewSurface {
        FriendsDrawerContent(
            uiState = sampleFriendsUiStateLoaded.copy(
                drawerTab = FriendDrawerTab.REQUESTED,
            ),
            onClose = {},
            onSelectTab = {},
            onSelectFriend = {},
            onOpenAddFriendBottomSheet = {},
            onOpenFriendDetail = {},
            onAcceptFriend = {},
            onDeclineFriend = {},
        )
    }
}

@SnuttPreview
@Composable
private fun FriendsDrawer_FriendsTab_Empty() {
    SnuttPreviewSurface {
        FriendsDrawerContent(
            uiState = sampleFriendsUiStateLoaded.copy(
                activeFriends = emptyList(),
                requestedFriends = emptyList(),
                selectedFriend = null,
            ),
            onClose = {},
            onSelectTab = {},
            onSelectFriend = {},
            onOpenAddFriendBottomSheet = {},
            onOpenFriendDetail = {},
            onAcceptFriend = {},
            onDeclineFriend = {},
        )
    }
}
