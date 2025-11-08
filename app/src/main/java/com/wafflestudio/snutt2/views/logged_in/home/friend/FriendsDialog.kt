package com.wafflestudio.snutt2.views.logged_in.home.friend

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.TipCloseIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.Friend
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import kotlinx.coroutines.launch

@Composable
fun FriendsDialogContent(
    dialogState: FriendDialogState,
    onDismiss: () -> Unit,
    onConfirmDeleteFriend: (Friend) -> Unit,
    onConfirmDeclineFriend: (Friend) -> Unit,
) {
    when (dialogState) {
        is FriendDialogState.None -> {
            // No dialog
        }

        is FriendDialogState.DeleteFriend -> {
            DeleteFriendDialog(
                friend = dialogState.friend,
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteFriend(dialogState.friend) },
            )
        }

        is FriendDialogState.DeclineFriend -> {
            DeclineFriendDialog(
                friend = dialogState.friend,
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeclineFriend(dialogState.friend) },
            )
        }

        is FriendDialogState.ShowGuide -> {
            GuideDialog(
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun DeleteFriendDialog(
    friend: Friend,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.friend_delete_dialog_title),
        positiveButtonText = stringResource(R.string.notifications_noti_delete),
        negativeButtonText = stringResource(R.string.common_cancel),
    ) {
        Text(
            text = stringResource(R.string.friend_delete_dialog_message),
            style = SNUTTTypography.body2,
        )
    }
}

@Composable
private fun DeclineFriendDialog(
    friend: Friend,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.friend_decline_dialog_title),
        positiveButtonText = stringResource(R.string.friend_decline),
        negativeButtonText = stringResource(R.string.common_cancel),
    ) {
        Text(
            text = "${friend.nickname.nickname}#${friend.nickname.tag}님의 친구 요청을 거절하시겠습니까?",
            style = SNUTTTypography.body2,
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun GuideDialog(
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })

    BoxWithConstraints {
        val width = maxWidth.times(0.8f)
        val height = width * (640f / 600)
        Dialog(onDismissRequest = onDismiss) {
            Surface(elevation = 10.dp) {
                Column(
                    modifier = Modifier
                        .width(width)
                        .height(height)
                        .background(SNUTTColors.White900),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TipCloseIcon(
                            modifier = Modifier
                                .size(15.dp)
                                .clicks { onDismiss() },
                            colorFilter = ColorFilter.tint(
                                if (pagerState.currentPage != 4) SNUTTColors.VacancyGray else SNUTTColors.Black900,
                            ),
                        )
                    }

                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp),
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .padding(horizontal = 17.dp)
                                .align(Alignment.Center)
                                .fillMaxWidth(),
                        ) { page ->
                            Image(
                                painter = painterResource(
                                    if (isDarkMode()) {
                                        when (page) {
                                            0 -> R.drawable.img_friend_intro_dark_0
                                            1 -> R.drawable.img_friend_intro_dark_1
                                            2 -> R.drawable.img_friend_intro_dark_2
                                            3 -> R.drawable.img_friend_intro_dark_3
                                            else -> R.drawable.img_friend_intro_dark_4
                                        }
                                    } else {
                                        when (page) {
                                            0 -> R.drawable.img_friend_intro_0
                                            1 -> R.drawable.img_friend_intro_1
                                            2 -> R.drawable.img_friend_intro_2
                                            3 -> R.drawable.img_friend_intro_3
                                            else -> R.drawable.img_friend_intro_4
                                        }
                                    },
                                ),
                                contentDescription = null,
                            )
                        }

                        if (pagerState.currentPage > 0) {
                            ArrowBackIcon(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .size(30.dp)
                                    .clicks {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    },
                                colorFilter = ColorFilter.tint(SNUTTColors.VacancyGray),
                            )
                        }
                        if (pagerState.currentPage < 4) {
                            RightArrowIcon(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(30.dp)
                                    .clicks {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    },
                                colorFilter = ColorFilter.tint(SNUTTColors.VacancyGray),
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.padding(bottom = 36.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(6.dp)
                                    .background(
                                        color = if (pagerState.currentPage == index) {
                                            when (index) {
                                                0 -> SNUTTColors.Red
                                                1 -> SNUTTColors.Grass
                                                2 -> SNUTTColors.Yellow
                                                3 -> SNUTTColors.Blue
                                                else -> SNUTTColors.Violet
                                            }
                                        } else {
                                            SNUTTColors.VacancyGray
                                        },
                                        shape = CircleShape,
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}
