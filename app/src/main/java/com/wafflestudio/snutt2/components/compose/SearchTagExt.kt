package com.wafflestudio.snutt2.components.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domainmodel.SearchTag

@Composable
fun SearchTag.displayName(): String = when (this) {
    SearchTag.TimeEmpty -> stringResource(R.string.search_tag_time_empty)
    SearchTag.TimeSelect -> stringResource(R.string.search_tag_time_select)
    SearchTag.EtcEng -> stringResource(R.string.search_tag_etc_eng)
    SearchTag.EtcMilitary -> stringResource(R.string.search_tag_etc_military)
    is SearchTag.Regular -> name
}
