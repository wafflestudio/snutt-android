package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun DrawerIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_drawer),
        contentDescription = ""
    )
}

@Composable
fun ListIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_lecture_list),
        contentDescription = ""
    )
}

@Composable
fun NotificationIcon(
    modifier: Modifier = Modifier,
    new: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(if (new) R.drawable.ic_alarm_active else R.drawable.ic_alarm_default),
        contentDescription = "",
        colorFilter = colorFilter
    )
}

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_share),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ArrowBackIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_arrow_back),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_search_unselected),
        contentDescription = "",
        colorFilter = colorFilter
    )
}

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_filter),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ExitIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(R.drawable.ic_exit),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun TagIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_tag),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ClockIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_clock),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun LocationIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_location),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun LogoIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.logo),
        contentDescription = ""
    )
}

@Composable
fun ArrowDownIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_arrow_down),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun VividCheckedIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_vivid_checked),
        contentDescription = ""
    )
}

@Composable
fun VividUncheckedIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_vivid_unchecked),
        contentDescription = ""
    )
}

@Composable
fun DuplicateIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_duplicate),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun MoreIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_more),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun WriteIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_write),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun TrashIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_trash),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun PaletteIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_palette),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun TimetableIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isSelected) R.drawable.ic_timetable_selected
            else R.drawable.ic_timetable_unselected
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isSelected) R.drawable.ic_search_selected
            else R.drawable.ic_search_unselected
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ReviewIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isSelected) R.drawable.ic_review_selected
            else R.drawable.ic_review_unselected
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun SettingIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isSelected) R.drawable.ic_setting_selected
            else R.drawable.ic_setting_unselected
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun TipCloseIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.btntipclose),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ArrowRight(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.arrowright),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun CheckedIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.checked),
        contentDescription = ""
    )
}

@Composable
fun WhiteCloseIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_close_white),
        contentDescription = ""
    )
}

@Composable
fun SendIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_send),
        contentDescription = ""
    )
}

@Composable
fun RedDot() {
    Canvas(modifier = Modifier.size(5.dp)) {
        drawCircle(SNUTTColors.Red)
    }
}

@Composable
fun BigSearchIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.img_search_big),
        contentDescription = ""
    )
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_warning),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun AlarmOnIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.tab_alarm_on),
        contentDescription = ""
    )
}

@Composable
fun LectureListIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_lecture_list),
        contentDescription = stringResource(R.string.home_timetable_drawer),
        colorFilter = colorFilter,
    )
}

@Composable
fun CalendarIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_calendar),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun RefreshIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_refresh),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}
