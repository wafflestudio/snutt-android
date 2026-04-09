package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode

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
fun ArrowUpIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_arrow_up),
        contentDescription = "",
        colorFilter = colorFilter,
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
fun WriteUnderlineIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_write_underline),
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
fun RedDotWithNumber(
    modifier: Modifier = Modifier,
    number: Long,
) {
    Canvas(
        modifier = modifier.size(16.dp),
    ) {
        drawCircle(
            color = SNUTTColors.Red,
            radius = size.minDimension / 2,
        )

        drawContext.canvas.nativeCanvas.apply {
            val text = number.toString()

            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                textSize = size.minDimension * 0.7f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val x = size.width / 2
            val y = size.height / 2 - (paint.descent() + paint.ascent()) / 2

            drawText(text, x, y, paint)
        }
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
fun RefreshTimeIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_refresh_time),
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
    dotSize: Dp = 5.dp,
    dotYOffset: Dp = 0.dp,
    color: Color = SNUTTColors.Red,
    content: @Composable (Modifier) -> Unit,
) {
    Box {
        content(Modifier.align(Alignment.Center))
        if (redDotExist) {
            Canvas(
                modifier = Modifier
                    .size(dotSize)
                    .align(Alignment.TopEnd)
                    .offset(y = dotYOffset),
            ) {
                drawCircle(color)
            }
        }
    }
}

@Composable
fun IconWithAlertDotNumber(
    modifier: Modifier = Modifier,
    redDotNumber: Long = 0,
    content: @Composable (Modifier) -> Unit,
) {
    Box {
        content(Modifier.align(Alignment.Center))
        if (redDotNumber > 0) {
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 9.dp, y = (-5).dp),
            ) {
                drawCircle(
                    color = SNUTTColors.Red,
                    radius = size.minDimension / 2,
                )

                drawContext.canvas.nativeCanvas.apply {
                    val text = redDotNumber.toString()

                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        textSize = size.minDimension * 0.7f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }

                    val x = size.width / 2
                    val y = size.height / 2 - (paint.descent() + paint.ascent()) / 2

                    drawText(text, x, y, paint)
                }
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
fun MagicIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_magic),
        contentDescription = "",
        colorFilter = colorFilter,
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
fun MapIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_map),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun ResetIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_reset),
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
        colorFilter = colorFilter,
    )
}

@Composable
fun NotificationVacancyIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(R.drawable.ic_ringing_alarm_notification),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun NotificationFriendIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(R.drawable.ic_ringing_alarm_notification),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun NotificationTrashIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(R.drawable.ic_trash_new),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun CustomThemeMoreIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(
            if (isDarkMode()) R.drawable.ic_custom_theme_more_dark else R.drawable.ic_custom_theme_more,
        ),
        contentDescription = "",
    )
}

@Composable
fun ArrowLeftBold(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.arrow_left_bold),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    if (filled) {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_star_filled),
            contentDescription = "",
            colorFilter = colorFilter,
        )
    } else {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.ic_star_outline),
            contentDescription = "",
            colorFilter = colorFilter,
        )
    }
}

@Composable
fun ChevronIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_arrow_right),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun AddFriendIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_user_add),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun KakaoTalkIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_kakao_talk),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}

@Composable
fun FriendHashIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    Image(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_friend_hash),
        contentDescription = "",
        colorFilter = colorFilter,
    )
}
