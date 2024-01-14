package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.VividCheckedIcon
import com.wafflestudio.snutt2.components.compose.VividUncheckedIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun TagsColumn(
    tagsByTagType: List<Selectable<TagDto>>,
    selectedTimes: State<List<List<Boolean>>>,
    baseAnimatedFloat: State<Float>,
    height: Dp,
    width: Dp,
    toggleTimeSelectTagTo: (Boolean) -> Unit,
    onToggleTag: (TagDto) -> Unit,
) {
    val configuration = LocalConfiguration.current

    val alphaAnimatedFloat by remember {
        derivedStateOf { 1f - baseAnimatedFloat.value }
    }
    val offsetXAnimatedDp by remember {
        derivedStateOf {
            val tagsColumnWidth =
                configuration.screenWidthDp - SearchOptionSheetConstants.TagColumnWidthDp
            return@derivedStateOf tagsColumnWidth.dp * baseAnimatedFloat.value
        }
    }

    LazyColumn(
        modifier = Modifier
            .offset(x = offsetXAnimatedDp)
            .alpha(alphaAnimatedFloat)
            .size(width, height)
            .padding(start = 20.dp, bottom = 10.dp),
        // FIXME: rememberLazyListState() 넣으면 오류
    ) {
        items(tagsByTagType) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clicks {
                            if (it.item == TagDto.TIME_SELECT) {
                                toggleTimeSelectTagTo(it.state.not())
                            } else {
                                onToggleTag(it.item)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (it.state) {
                        VividCheckedIcon(modifier = Modifier.size(15.dp))
                    } else {
                        VividUncheckedIcon(modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = it.item.name,
                        style = SNUTTTypography.body1,
                    )
                }
                if (it.item == TagDto.TIME_SELECT) {
                    timeSlotsToFormattedString(selectedTimes.value).let {
                        if (it.isNotEmpty()) {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(start = 25.dp)
                                    .clicks {
                                        toggleTimeSelectTagTo(true)
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
    }
}
