package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
internal fun BottomNavigation(
    pageState: HomeItem,
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
            TimetableIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState == HomeItem.Timetable,
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
            SearchIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState == HomeItem.Search,
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
            ReviewIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState is HomeItem.Review,
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
            BigPeopleIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState is HomeItem.Friends,
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
            HorizontalMoreIcon(
                modifier = Modifier.size(30.dp),
                isSelected = pageState == HomeItem.Settings,
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }
    }
}
