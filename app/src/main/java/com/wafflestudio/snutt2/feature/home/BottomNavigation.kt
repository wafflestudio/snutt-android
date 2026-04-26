package com.wafflestudio.snutt2.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.BorderButton
import com.wafflestudio.snutt2.ui.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
internal fun BottomNavigation(
    pageState: HomeItem,
    uncheckedNotificationExist: Boolean,
    onUpdatePageState: (HomeItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(SNUTTColors.White900),
    ) {
        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Timetable)
            },
        ) {
            SnuttIcon(
                if (pageState == HomeItem.Timetable) R.drawable.ic_timetable_selected else R.drawable.ic_timetable_unselected,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }

        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Search)
            },
        ) {
            SnuttIcon(
                if (pageState == HomeItem.Search) R.drawable.ic_search_selected else R.drawable.ic_search_unselected,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }

        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Review())
            },
        ) {
            SnuttIcon(
                if (pageState is HomeItem.Review) R.drawable.ic_review_selected else R.drawable.ic_review_unselected,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }

        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Friends)
            },
        ) {
            SnuttIcon(
                if (pageState is HomeItem.Friends) R.drawable.ic_people_selected else R.drawable.ic_people_unselected,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }

        BorderButton(
            color = SNUTTColors.White900,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            onClick = {
                onUpdatePageState(HomeItem.Settings)
            },
        ) {
            IconWithAlertDot(
                redDotExist = uncheckedNotificationExist,
                dotSize = 4.dp,
                dotYOffset = 3.dp,
            ) {
                SnuttIcon(
                    if (pageState == HomeItem.Settings) R.drawable.ic_horizontal_more_selected else R.drawable.ic_horizontal_more_unselected,
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun BottomNavigation_TimetableSelected() {
    SnuttPreviewSurface {
        BottomNavigation(
            pageState = HomeItem.Timetable,
            uncheckedNotificationExist = false,
            onUpdatePageState = {},
        )
    }
}

@SnuttPreview
@Composable
private fun BottomNavigation_SettingsSelectedWithDot() {
    SnuttPreviewSurface {
        BottomNavigation(
            pageState = HomeItem.Settings,
            uncheckedNotificationExist = true,
            onUpdatePageState = {},
        )
    }
}
