package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun DrawerIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_drawer),
        contentDescription = stringResource(R.string.home_timetable_drawer),
        colorFilter = colorFilter,
    )
}

@Composable
fun ListIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_lecture_list),
        contentDescription = "",
    )
}

@Composable
fun NotificationIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(R.drawable.ic_alarm_default),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier.size(30.dp),
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_search_unselected),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "",
    )
}

@Composable
fun ArrowDownIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_vivid_checked),
        contentDescription = "",
    )
}

@Composable
fun VividUncheckedIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_vivid_unchecked),
        contentDescription = "",
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_palette),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun PinIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_pin),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun PinOffIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_pin_off),
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
            if (isSelected) {
                R.drawable.ic_timetable_selected
            } else {
                R.drawable.ic_timetable_unselected
            },
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
            if (isSelected) {
                R.drawable.ic_search_selected
            } else {
                R.drawable.ic_search_unselected
            },
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
            if (isSelected) {
                R.drawable.ic_review_selected
            } else {
                R.drawable.ic_review_unselected
            },
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun BigPeopleIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isSelected) {
                R.drawable.ic_people_selected
            } else {
                R.drawable.ic_people_unselected
            },
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun PeopleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_people_on),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun PeopleOffIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_people_off),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ThickReviewIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_review_thick),
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
            if (isSelected) {
                R.drawable.ic_setting_selected
            } else {
                R.drawable.ic_setting_unselected
            },
        ),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun HorizontalMoreIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isSelected) {
                R.drawable.ic_horizontal_more_selected
            } else {
                R.drawable.ic_horizontal_more_unselected
            },
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
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.checked),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_close),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun SendIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_send),
        colorFilter = colorFilter,
        contentDescription = "",
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
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.img_search_big),
        contentDescription = "",
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
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.tab_alarm_on),
        contentDescription = "",
    )
}

@Composable
fun LectureListIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
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

@Composable
fun BookmarkIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
    marked: Boolean = false,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = if (marked) R.drawable.ic_bookmark_selected else R.drawable.ic_bookmark_unselected),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun BookmarkPageIcon(
    modifier: Modifier = Modifier.size(30.dp),
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_bookmark_page),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun IconWithAlertDot(
    redDotExist: Boolean = false,
    color: Color = SNUTTColors.Red,
    content: @Composable (Modifier) -> Unit,
) {
    Box {
        content(Modifier.align(Alignment.Center))
        if (redDotExist) {
            Canvas(
                modifier = Modifier
                    .size(5.dp)
                    .align(Alignment.TopEnd),
            ) {
                drawCircle(color)
            }
        }
    }
}

@Composable
fun RightArrowIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_arrow_right),
        contentDescription = "add arrow",
        colorFilter = colorFilter,
    )
}

@Composable
fun RemarkIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_remark),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun PersonIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_person),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun DetailIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_detail),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun RingingAlarmIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
    marked: Boolean = false,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = if (marked) R.drawable.ic_ringing_alarm_selected else R.drawable.ic_ringing_alarm_unselected),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun AddCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_add_circle),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun RemoveCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_remove_circle),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun QuestionCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_question_circle),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun CloseCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_close_circle),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun MegaphoneIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_megaphone),
        contentDescription = "",
        colorFilter = colorFilter
    )
}

@Composable
fun AddIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_add),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun CustomThemePinIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colors.surface, shape = CircleShape),
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colors.secondary, shape = CircleShape)
                .zIndex(1f)
                .align(Alignment.Center),
        )
        Image(
            painter = painterResource(R.drawable.ic_pin_bold),
            contentDescription = null,
            modifier = Modifier
                .zIndex(2f)
                .align(Alignment.Center),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary),
        )
    }
}
