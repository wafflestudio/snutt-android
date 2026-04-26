package com.wafflestudio.snutt2.feature.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.Friend
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.FriendHashIcon
import com.wafflestudio.snutt2.ui.components.compose.KakaoTalkIcon
import com.wafflestudio.snutt2.ui.components.compose.MoreActionItem
import com.wafflestudio.snutt2.ui.components.compose.TrashIcon
import com.wafflestudio.snutt2.ui.components.compose.WarningIcon
import com.wafflestudio.snutt2.ui.components.compose.WriteIcon
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun FriendsBottomSheetContent(
    bottomSheetContent: FriendBottomSheetContent,
    onDismiss: () -> Unit,
    onRequestWithNickname: () -> Unit,
    onRequestWithKakaoTalk: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onSubmitNickname: () -> Unit,
    onDeleteFriend: (Friend) -> Unit,
    onEditDisplayName: (Friend) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSubmitDisplayName: () -> Unit,
) {
    when (bottomSheetContent) {
        is FriendBottomSheetContent.Hidden -> {}

        is FriendBottomSheetContent.RequestMethodList -> {
            AddFriendMethodListBottomSheet(
                onRequestWithNickName = onRequestWithNickname,
                onRequestWithKakaoTalk = onRequestWithKakaoTalk,
            )
        }

        is FriendBottomSheetContent.RequestWithNickname -> {
            RequestWithNicknameBottomSheet(
                nickname = bottomSheetContent.nickname,
                onNicknameChange = onNicknameChange,
                onSubmit = onSubmitNickname,
                onDismiss = onDismiss,
            )
        }

        is FriendBottomSheetContent.FriendDetail -> {
            FriendDetailBottomSheet(
                friend = bottomSheetContent.friend,
                onEditDisplayName = { onEditDisplayName(bottomSheetContent.friend) },
                onDeleteFriend = { onDeleteFriend(bottomSheetContent.friend) },
                onDismiss = onDismiss,
            )
        }

        is FriendBottomSheetContent.EditDisplayName -> {
            EditDisplayNameBottomSheet(
                friend = bottomSheetContent.friend,
                displayName = bottomSheetContent.displayName,
                onDisplayNameChange = onDisplayNameChange,
                onSubmit = onSubmitDisplayName,
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun AddFriendMethodListBottomSheet(
    onRequestWithKakaoTalk: () -> Unit,
    onRequestWithNickName: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(vertical = 12.dp),
    ) {
        MoreActionItem(
            icon = {
                KakaoTalkIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Gray600),
                )
            },
            text = stringResource(R.string.friend_add_kakao),
        ) {
            onRequestWithKakaoTalk()
        }
        MoreActionItem(
            icon = {
                FriendHashIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Gray600),
                )
            },
            text = stringResource(R.string.friend_add_nickname),
        ) {
            onRequestWithNickName()
        }
    }
}

@Composable
private fun RequestWithNicknameBottomSheet(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(25.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.common_cancel),
                style = SNUTTTypography.body1,
                modifier = Modifier.clickable { onDismiss() },
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.friend_request_send),
                style = if (nickname.isNotBlank()) {
                    SNUTTTypography.body1
                } else {
                    SNUTTTypography.body1.copy(color = SNUTTColors.Gray200)
                },
                modifier = Modifier.clickable(enabled = nickname.isNotBlank()) {
                    onSubmit()
                },
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.friend_request_nickname_label),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Gray600),
        )
        Spacer(modifier = Modifier.height(15.dp))
        EditText(
            value = nickname,
            onValueChange = onNicknameChange,
            hint = stringResource(R.string.friend_request_nickname_hint),
            underlineColor = SNUTTColors.SNUTTTheme,
            underlineColorFocused = SNUTTColors.SNUTTTheme,
            underlineWidth = 2.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(6.5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WarningIcon(
                modifier = Modifier.size(18.dp),
                colorFilter = if (nickname.isNotBlank()) ColorFilter.tint(SNUTTColors.SNUTTTheme) else null,
            )
            Text(
                text = stringResource(R.string.friend_request_nickname_error),
                style = SNUTTTypography.body2.copy(color = if (nickname.isNotBlank()) SNUTTColors.SNUTTTheme else SNUTTColors.Gray600),
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun FriendDetailBottomSheet(
    friend: Friend,
    onEditDisplayName: () -> Unit,
    onDeleteFriend: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(vertical = 12.dp),
    ) {
        MoreActionItem(
            icon = { WriteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.friend_display_name_title),
        ) {
            onEditDisplayName()
        }
        MoreActionItem(
            icon = { TrashIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.friend_delete_from_list),
        ) {
            onDeleteFriend()
        }
    }
}

@Composable
private fun EditDisplayNameBottomSheet(
    friend: Friend,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isModifiable = displayName != (friend.displayName ?: "") && displayName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(25.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.common_cancel),
                style = SNUTTTypography.body1,
                modifier = Modifier.clickable { onDismiss() },
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.friend_apply),
                style = if (isModifiable) {
                    SNUTTTypography.body1
                } else {
                    SNUTTTypography.body1.copy(color = SNUTTColors.Gray200)
                },
                modifier = Modifier.clickable(enabled = isModifiable) {
                    onSubmit()
                },
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Row {
            Text(
                text = stringResource(R.string.friend_display_name_label),
                style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Gray600),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.friend_display_name_char_limit),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Gray600),
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        EditText(
            value = displayName,
            onValueChange = onDisplayNameChange,
            underlineColor = SNUTTColors.SNUTTTheme,
            underlineColorFocused = SNUTTColors.SNUTTTheme,
            underlineWidth = 2.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = stringResource(
                R.string.friend_display_name_original,
                "${friend.nickname.nickname}#${friend.nickname.tag}",
            ),
            style = SNUTTTypography.body2.copy(color = SNUTTColors.SNUTTTheme),
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@SnuttPreview
@Composable
private fun FriendAdd_MethodList() {
    SnuttPreviewSurface {
        AddFriendMethodListBottomSheet(
            onRequestWithNickName = {},
            onRequestWithKakaoTalk = {},
        )
    }
}

@SnuttPreview
@Composable
private fun FriendAdd_NicknameInput_Empty() {
    SnuttPreviewSurface {
        RequestWithNicknameBottomSheet(
            nickname = "",
            onNicknameChange = {},
            onSubmit = {},
            onDismiss = {},
        )
    }
}

@SnuttPreview
@Composable
private fun FriendAdd_NicknameInput_Filled() {
    SnuttPreviewSurface {
        RequestWithNicknameBottomSheet(
            nickname = "홍길동#1234",
            onNicknameChange = {},
            onSubmit = {},
            onDismiss = {},
        )
    }
}

@SnuttPreview
@Composable
private fun FriendDetail_ActionList() {
    SnuttPreviewSurface {
        FriendDetailBottomSheet(
            friend = PreviewData.sampleFriends.first(),
            onEditDisplayName = {},
            onDeleteFriend = {},
            onDismiss = {},
        )
    }
}

@SnuttPreview
@Composable
private fun DisplayNameEdit_Filled() {
    SnuttPreviewSurface {
        EditDisplayNameBottomSheet(
            friend = PreviewData.sampleFriends.first(),
            displayName = "김철수",
            onDisplayNameChange = {},
            onSubmit = {},
            onDismiss = {},
        )
    }
}

@SnuttPreview
@Composable
private fun DisplayNameEdit_Empty() {
    SnuttPreviewSurface {
        EditDisplayNameBottomSheet(
            friend = PreviewData.sampleFriends[1],
            displayName = "",
            onDisplayNameChange = {},
            onSubmit = {},
            onDismiss = {},
        )
    }
}
