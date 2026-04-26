package com.wafflestudio.snutt2.feature.home.timetable

import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.DrawerIcon
import com.wafflestudio.snutt2.ui.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.components.compose.NotificationVacancyIcon
import com.wafflestudio.snutt2.ui.components.compose.SearchIcon
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun TimetableMoreAction(
    onClickAddBySearch: () -> Unit,
    onClickAddManually: () -> Unit,
    onClickTableLecturesListIcon: () -> Unit,
    onClickVacancyIcon: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -45f,
        animationSpec = spring(),
    )

    ExitIcon(
        modifier = Modifier
            .size(30.dp)
            .graphicsLayer { rotationZ = iconRotation }
            .clicks { expanded = true },
    )

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
            SideEffect {
                dialogWindow?.setWindowAnimations(0)
                dialogWindow?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                dialogWindow?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
            TimetableDropdownOverlay(
                onDismiss = { expanded = false },
                onClickAddBySearch = onClickAddBySearch,
                onClickAddManually = onClickAddManually,
                onClickTableLecturesListIcon = onClickTableLecturesListIcon,
                onClickVacancyIcon = onClickVacancyIcon,
            )
        }
    }
}

@Composable
fun TimetableDropdownOverlay(
    onDismiss: () -> Unit,
    onClickAddBySearch: () -> Unit,
    onClickAddManually: () -> Unit,
    onClickTableLecturesListIcon: () -> Unit,
    onClickVacancyIcon: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SNUTTColors.Black.copy(alpha = 0.4F))
                .clicks { onDismiss() },
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 58.dp, end = 12.dp)
                .background(
                    color = SNUTTColors.DropdownMenuBackground,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(start = 16.dp, end = 32.dp, top = 6.dp, bottom = 10.dp),
        ) {
            Text(
                modifier = Modifier.clicks {}.padding(horizontal = 2.dp, vertical = 6.dp),
                text = stringResource(R.string.home_dropdown_menu_title_add_lecture),
                style = SNUTTTypography.subtitle1.copy(
                    color = SNUTTColors.TextMed,
                    fontSize = 11.sp,
                ),
            )
            Row(
                modifier = Modifier.clicks {
                    onDismiss()
                    onClickAddBySearch()
                }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SearchIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
                Text(
                    text = stringResource(R.string.home_dropdown_menu_content_add_lecture_by_search),
                    style = SNUTTTypography.h3.copy(fontWeight = FontWeight.Normal),
                )
            }
            Row(
                modifier = Modifier.clicks {
                    onDismiss()
                    onClickAddManually()
                }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SnuttIcon(R.drawable.ic_write_underline, modifier = Modifier.size(22.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                Text(
                    text = stringResource(R.string.home_dropdown_menu_content_add_lecture_manually),
                    style = SNUTTTypography.h3.copy(fontWeight = FontWeight.Normal),
                )
            }
            Text(
                modifier = Modifier.clicks {}.padding(horizontal = 2.dp, vertical = 6.dp),
                text = stringResource(R.string.home_dropdown_menu_title_lecture_list),
                style = SNUTTTypography.subtitle1.copy(
                    color = SNUTTColors.TextMed,
                    fontSize = 11.sp,
                ),
            )
            Row(
                modifier = Modifier.clicks {
                    onDismiss()
                    onClickTableLecturesListIcon()
                }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DrawerIcon(modifier = Modifier.size(22.dp))
                Text(
                    text = stringResource(R.string.home_dropdown_menu_content_current_timetable_lecture_list),
                    style = SNUTTTypography.h3.copy(fontWeight = FontWeight.Normal),
                )
            }
            Row(
                modifier = Modifier.clicks {
                    onDismiss()
                    onClickVacancyIcon()
                }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NotificationVacancyIcon(modifier = Modifier.size(22.dp))
                Text(
                    text = stringResource(R.string.home_dropdown_menu_content_vacancy_lecture_list),
                    style = SNUTTTypography.h3.copy(fontWeight = FontWeight.Normal),
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun TimetableDropdownOverlay_Default() {
    SnuttPreviewSurface {
        TimetableDropdownOverlay(
            onDismiss = {},
            onClickAddBySearch = {},
            onClickAddManually = {},
            onClickTableLecturesListIcon = {},
            onClickVacancyIcon = {},
        )
    }
}
