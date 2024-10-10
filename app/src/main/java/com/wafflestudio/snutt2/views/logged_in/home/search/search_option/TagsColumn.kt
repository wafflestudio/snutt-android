package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.VividCheckedIcon
import com.wafflestudio.snutt2.components.compose.VividUncheckedIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun TagsColumn(
    modifier: Modifier,
    recentSearchedDepartments: List<Selectable<TagDto>>,
    tagsByTagType: List<Selectable<TagDto>>,
    selectedTimes: State<List<List<Boolean>>>,
    baseAnimatedFloat: State<Float>,
    onToggleTag: (TagDto) -> Unit,
    onRemoveRecent: (TagDto) -> Unit,
    openTimeSelectSheet: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val alphaAnimatedFloat = 1f - baseAnimatedFloat.value
    val offsetXAnimatedDp = (configuration.screenWidthDp - SearchOptionSheetConstants.TagColumnWidthDp).dp * baseAnimatedFloat.value

    LazyColumn(
        modifier = modifier
            .offset(x = offsetXAnimatedDp)
            .alpha(alphaAnimatedFloat)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        // FIXME: rememberLazyListState() 넣으면 오류
    ) {
        if (recentSearchedDepartments.isNotEmpty()) {
            item {
                Text(
                    text = "최근 찾아본 학과",
                    style = SNUTTTypography.body1.copy(
                        fontSize = 13.sp,
                        color = SNUTTColors.Gray600,
                    )
                )
            }

            items(recentSearchedDepartments.reversed()) { departmentTag ->
                SelectableTagItem(
                    selectableTag = departmentTag,
                    selectedTimes = selectedTimes,
                    onToggleTag = onToggleTag,
                    onRemoveRecent = onRemoveRecent,
                    openTimeSelectSheet = openTimeSelectSheet,
                )
            }

            item {
                Divider(
                    modifier = Modifier
                        .padding(top = 2.dp, bottom = 8.dp),
                    thickness = 0.5f.dp,
                    color = SNUTTColors.Gray200,
                )
            }
        }

        items(tagsByTagType) { tag ->
            SelectableTagItem(
                selectableTag = tag,
                selectedTimes = selectedTimes,
                onToggleTag = onToggleTag,
                openTimeSelectSheet = openTimeSelectSheet,
            )
        }
    }
}

@Composable
fun SelectableTagItem(
    selectableTag: Selectable<TagDto>,
    selectedTimes: State<List<List<Boolean>>>,
    onToggleTag: (TagDto) -> Unit,
    onRemoveRecent: ((TagDto) -> Unit)? = null,
    openTimeSelectSheet: () -> Unit,
){
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row (
                modifier = Modifier
                    .clicks { onToggleTag(selectableTag.item) }
                    .weight(.1f),
                verticalAlignment = Alignment.CenterVertically,
            ){
                if (selectableTag.state) {
                    VividCheckedIcon(modifier = Modifier.size(15.dp))
                } else {
                    VividUncheckedIcon(modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = selectableTag.item.name,
                    style = SNUTTTypography.body1,
                )
            }

            if (onRemoveRecent != null) {
                Row (
                    modifier = Modifier
                        .clicks { onRemoveRecent(selectableTag.item) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ExitIcon(modifier = Modifier.size(18.dp))
                }
            }
        }
        if (selectableTag.item == TagDto.TIME_SELECT) {
            Spacer(modifier = Modifier.height(6.dp))
            timeSlotsToFormattedString(context, selectedTimes.value).let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(start = 25.dp)
                            .clicks {
                                openTimeSelectSheet()
                            },
                        style = SNUTTTypography.body2.copy(
                            color = SNUTTColors.Gray600,
                            textDecoration = TextDecoration.Underline,
                        ),
                    )
                }
            }
        }
    }
}
