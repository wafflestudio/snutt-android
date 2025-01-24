package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.clicks

@Composable
fun SearchOptionCancelButton(
    hideBottomSheet: () -> Unit,
) {
    Row(
        modifier = Modifier.clicks { hideBottomSheet() },
    ) {
        ExitIcon()
    }
}
