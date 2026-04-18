package com.wafflestudio.snutt2.feature.search.searchoption

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.ui.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.components.compose.VividCheckedIcon
import com.wafflestudio.snutt2.ui.components.compose.VividUncheckedIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.components.compose.displayName
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun SearchTagsColumn(
    recentSearchedDepartments: List<Selectable<SearchTag>>,
    searchTags: List<Selectable<SearchTag>>,
    selectedTimes: List<List<Boolean>>,
    onToggleTag: (SearchTag) -> Unit,
    onRemoveRecentSearchedDepartment: (SearchTag) -> Unit,
    openTimeSelectSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (recentSearchedDepartments.isNotEmpty()) {
            item {
                Text(
                    text = "최근 찾아본 학과",
                    style = SNUTTTypography.body1.copy(
                        fontSize = 13.sp,
                        color = SNUTTColors.Gray600,
                    ),
                )
            }

            items(recentSearchedDepartments.reversed()) { departmentTag ->
                SelectableTagItem(
                    selectableTag = departmentTag,
                    selectedTimes = selectedTimes,
                    onToggleTag = onToggleTag,
                    onRemoveRecent = onRemoveRecentSearchedDepartment,
                    openTimeSelectSheet = openTimeSelectSheet,
                )
            }

            item {
                Divider(
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                    thickness = 0.5f.dp,
                    color = SNUTTColors.Gray200,
                )
            }
        }

        items(searchTags) { tag ->
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
private fun SelectableTagItem(
    selectableTag: Selectable<SearchTag>,
    selectedTimes: List<List<Boolean>>,
    onToggleTag: (SearchTag) -> Unit,
    onRemoveRecent: ((SearchTag) -> Unit)? = null,
    openTimeSelectSheet: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(bottom = 6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .clicks { onToggleTag(selectableTag.item) }
                    .weight(.1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selectableTag.state) {
                    VividCheckedIcon(modifier = Modifier.size(15.dp))
                } else {
                    VividUncheckedIcon(modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = selectableTag.item.displayName(),
                    style = SNUTTTypography.body1,
                )
            }

            if (onRemoveRecent != null) {
                Row(
                    modifier = Modifier.clicks { onRemoveRecent(selectableTag.item) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ExitIcon(modifier = Modifier.size(18.dp))
                }
            }
        }
        if (selectableTag.item == SearchTag.TimeSelect) {
            Spacer(modifier = Modifier.height(6.dp))
            timeSlotsToFormattedString(context, selectedTimes).let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(start = 25.dp)
                            .clicks { openTimeSelectSheet() },
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

// region Preview

@Preview(showBackground = true)
@Composable
private fun SearchTagsColumnPreview() {
    val sampleTags: List<Selectable<SearchTag>> = listOf(
        DataWithState(SearchTag.Regular(TagType.DEPARTMENT, "수리과학부"), true),
        DataWithState(SearchTag.Regular(TagType.DEPARTMENT, "조선해양공학과"), false),
        DataWithState(SearchTag.Regular(TagType.DEPARTMENT, "종교학괴"), false),
    )
    val sampleRecent: List<Selectable<SearchTag>> = listOf(
        DataWithState(SearchTag.Regular(TagType.DEPARTMENT, "컴퓨터공학부"), false),
        DataWithState(SearchTag.Regular(TagType.DEPARTMENT, "전기·정보공학부"), true),
    )

    SearchTagsColumn(
        recentSearchedDepartments = sampleRecent,
        searchTags = sampleTags,
        selectedTimes = emptyList(),
        onToggleTag = {},
        onRemoveRecentSearchedDepartment = {},
        openTimeSelectSheet = {},
        modifier = Modifier.width(250.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchTagsColumnPreview_TimeSelect() {
    val sampleTags: List<Selectable<SearchTag>> = listOf(
        DataWithState(SearchTag.TimeEmpty, false),
        DataWithState(SearchTag.TimeSelect, true),
    )
    val sampleTimeSlots = List(7) { day ->
        List(30) { slot -> day == 0 && slot in 2..5 }
    }

    SearchTagsColumn(
        recentSearchedDepartments = emptyList(),
        searchTags = sampleTags,
        selectedTimes = sampleTimeSlots,
        onToggleTag = {},
        onRemoveRecentSearchedDepartment = {},
        openTimeSelectSheet = {},
        modifier = Modifier.width(250.dp),
    )
}

// endregion
