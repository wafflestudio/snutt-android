package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> AnimatedLazyRow(
    modifier: Modifier = Modifier,
    itemList: List<T>,
    itemKey: ((T) -> Any)?,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    AnimatedVisibility(visible = itemList.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(items = itemList, key = itemKey) {
                itemContent(it)
            }
        }
    }
}
