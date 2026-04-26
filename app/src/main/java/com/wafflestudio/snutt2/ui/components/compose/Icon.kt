package com.wafflestudio.snutt2.ui.components.compose

import androidx.annotation.DrawableRes
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
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode

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
fun ListIcon(
    modifier: Modifier = Modifier,
) {
    SnuttIcon(
        id = R.drawable.ic_lecture_list,
        modifier = modifier.size(30.dp),
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
fun SearchIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_search_unselected,
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
fun TagIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_tag,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun ClockIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_clock,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun LocationIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_location,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun LogoIcon(
    modifier: Modifier = Modifier,
) {
    SnuttIcon(
        id = R.drawable.logo,
        modifier = modifier,
    )
}

@Composable
fun ArrowUpIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_arrow_up,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun ArrowDownIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_arrow_down,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun VividCheckedIcon(
    modifier: Modifier = Modifier,
) {
    SnuttIcon(
        id = R.drawable.ic_vivid_checked,
        modifier = modifier,
    )
}

@Composable
fun VividUncheckedIcon(
    modifier: Modifier = Modifier,
) {
    SnuttIcon(
        id = R.drawable.ic_vivid_unchecked,
        modifier = modifier,
    )
}

@Composable
fun DuplicateIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_duplicate,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun MoreIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_more,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun WriteIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_write,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun WriteUnderlineIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_write_underline,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun TrashIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_trash,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun PaletteIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_palette,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun PinIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_pin,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun PinOffIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_pin_off,
        modifier = modifier,
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
fun PeopleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_people_on,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun PeopleOffIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_people_off,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun ThickReviewIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_review_thick,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun SettingIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = if (isSelected) R.drawable.ic_setting_selected else R.drawable.ic_setting_unselected,
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
fun TipCloseIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.btntipclose,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun ArrowRight(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.arrowright,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun CheckedIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.checked,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun CloseIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_close,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun SendIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_send,
        modifier = modifier,
        colorFilter = colorFilter,
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
    SnuttIcon(
        id = R.drawable.img_search_big,
        modifier = modifier,
    )
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_warning,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun AlarmOnIcon(
    modifier: Modifier = Modifier,
) {
    SnuttIcon(
        id = R.drawable.tab_alarm_on,
        modifier = modifier,
    )
}

@Composable
fun LectureListIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_lecture_list,
        modifier = modifier,
        colorFilter = colorFilter,
        contentDescription = stringResource(R.string.home_timetable_drawer),
    )
}

@Composable
fun CalendarIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_calendar,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun RefreshTimeIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_refresh_time,
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
fun BookmarkPageIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_bookmark_page,
        modifier = modifier.size(30.dp),
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
    SnuttIcon(
        id = R.drawable.ic_arrow_right,
        modifier = modifier,
        colorFilter = colorFilter,
        contentDescription = "add arrow",
    )
}

@Composable
fun RemarkIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(SNUTTColors.Black900),
) {
    SnuttIcon(
        id = R.drawable.ic_remark,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun PersonIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_person,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun DetailIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_detail,
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
fun AddCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_add_circle,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun RemoveCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_remove_circle,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun QuestionCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_question_circle,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun CloseCircleIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_close_circle,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun MagicIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_magic,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun AddIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_add,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun MapIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_map,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun ResetIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_reset,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun MegaphoneIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_megaphone,
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
fun CustomThemeMoreIcon(
    modifier: Modifier = Modifier,
) {
    SnuttIcon(
        id = if (isDarkMode()) R.drawable.ic_custom_theme_more_dark else R.drawable.ic_custom_theme_more,
        modifier = modifier,
    )
}

@Composable
fun ArrowLeftBold(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.arrow_left_bold,
        modifier = modifier,
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

@Composable
fun ChevronIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_arrow_right,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun AddFriendIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_user_add,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun KakaoTalkIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_kakao_talk,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@Composable
fun FriendHashIcon(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null,
) {
    SnuttIcon(
        id = R.drawable.ic_friend_hash,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}

@SnuttPreview
@Composable
private fun RedDot_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
            RedDot()
        }
    }
}

@SnuttPreview
@Composable
private fun RedDotWithNumber_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
            RedDotWithNumber(number = 3)
        }
    }
}

@SnuttPreview
@Composable
private fun IconWithAlertDot_Default() {
    SnuttPreviewSurface {
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            IconWithAlertDot(redDotExist = true) {
                NotificationIcon(modifier = it.size(30.dp))
            }
        }
    }
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
