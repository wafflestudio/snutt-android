package com.wafflestudio.snutt2.views.logged_in.home.friend

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.FriendHashIcon
import com.wafflestudio.snutt2.components.compose.KakaoTalkIcon
import com.wafflestudio.snutt2.components.compose.MoreActionItem
import com.wafflestudio.snutt2.components.compose.TrashIcon
import com.wafflestudio.snutt2.components.compose.WarningIcon
import com.wafflestudio.snutt2.components.compose.WriteIcon
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun FriendsBottomSheetContent(
    bottomSheetContent: FriendBottomSheetContent,
    onDismiss: () -> Unit,
    onRequestWithNickname: () -> Unit,
    onRequestWithKakaoTalk: () -> Unit,
    onSubmitNickname: (String) -> Unit,
    onDeleteFriend: (Friend) -> Unit,
    onEditDisplayName: (Friend) -> Unit,
    onSubmitDisplayName: (Friend, String) -> Unit,
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
                initialNickname = bottomSheetContent.nickname,
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
                initialDisplayName = bottomSheetContent.displayName,
                onSubmit = { newDisplayName ->
                    onSubmitDisplayName(bottomSheetContent.friend, newDisplayName)
                },
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
    initialNickname: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var nickname by remember { mutableStateOf(initialNickname) }

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
                    onSubmit(nickname)
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
            onValueChange = { nickname = it },
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
    initialDisplayName: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var displayName by remember { mutableStateOf(initialDisplayName) }
    val isModifiable = displayName != initialDisplayName && displayName.isNotBlank()

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
                    onSubmit(displayName)
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
                text = "(공백 포함 한/영/숫자 10자 이내)",
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Gray600),
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        EditText(
            value = displayName,
            onValueChange = { displayName = it },
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

@Preview(showBackground = true)
@Composable
private fun RequestMethodListBottomSheetPreview() {
    AddFriendMethodListBottomSheet(
        onRequestWithNickName = {},
        onRequestWithKakaoTalk = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun RequestWithNicknameBottomSheetPreview() {
    RequestWithNicknameBottomSheet(
        initialNickname = "",
        onSubmit = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun RequestWithNicknameBottomSheetFilledPreview() {
    RequestWithNicknameBottomSheet(
        initialNickname = "홍길동#1234",
        onSubmit = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun FriendDetailBottomSheetPreview() {
    FriendDetailBottomSheet(
        friend = PreviewData.sampleFriends.first(),
        onEditDisplayName = {},
        onDeleteFriend = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun EditDisplayNameBottomSheetPreview() {
    EditDisplayNameBottomSheet(
        friend = PreviewData.sampleFriends.first(),
        initialDisplayName = "김철수",
        onSubmit = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun EditDisplayNameBottomSheetEmptyPreview() {
    EditDisplayNameBottomSheet(
        friend = PreviewData.sampleFriends[1],
        initialDisplayName = "",
        onSubmit = {},
        onDismiss = {},
    )
}
