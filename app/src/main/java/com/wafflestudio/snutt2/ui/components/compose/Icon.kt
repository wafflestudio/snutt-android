package com.wafflestudio.snutt2.ui.components.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun SnuttIcon(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    contentDescription: String? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = id),
        contentDescription = contentDescription,
        colorFilter = colorFilter,
    )
}

@Composable
fun DrawerIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_drawer,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
        contentDescription = stringResource(R.string.home_timetable_drawer),
    )
}

@Composable
fun NotificationIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_alarm_default,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_share,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun ArrowBackIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_arrow_back,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_filter,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun ExitIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_exit,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun TimetableIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (isSelected) R.drawable.ic_timetable_selected else R.drawable.ic_timetable_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (isSelected) R.drawable.ic_search_selected else R.drawable.ic_search_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun ReviewIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (isSelected) R.drawable.ic_review_selected else R.drawable.ic_review_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun BigPeopleIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (isSelected) R.drawable.ic_people_selected else R.drawable.ic_people_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun HorizontalMoreIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (isSelected) R.drawable.ic_horizontal_more_selected else R.drawable.ic_horizontal_more_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun BookmarkIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
    marked: Boolean = false,
) {
    SnuttIcon(
        id = if (marked) R.drawable.ic_bookmark_selected else R.drawable.ic_bookmark_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun RingingAlarmIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    marked: Boolean = false,
) {
    SnuttIcon(
        id = if (marked) R.drawable.ic_ringing_alarm_selected else R.drawable.ic_ringing_alarm_unselected,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun NotificationVacancyIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_ringing_alarm_notification,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun NotificationFriendIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_ringing_alarm_notification,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun NotificationTrashIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_trash_new,
        modifier = modifier.size(30.dp),
        colorFilter = colorFilter,
    )
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (filled) R.drawable.ic_star_filled else R.drawable.ic_star_outline,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@SnuttPreview
@Composable
private fun StarIcon_Filled() {
    SnuttPreviewSurface {
        StarIcon(modifier = Modifier.size(24.dp), filled = true)
    }
}

@SnuttPreview
@Composable
private fun StarIcon_Empty() {
    SnuttPreviewSurface {
        StarIcon(modifier = Modifier.size(24.dp), filled = false)
    }
}
