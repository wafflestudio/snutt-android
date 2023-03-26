package com.wafflestudio.snutt2.components.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> AnimatedLazyRow(
    modifier: Modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth(),
    itemList: List<T>,
    itemKey: ((T) -> Any)?,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    AnimatedVisibility(visible = itemList.isNotEmpty()) {
        LazyRow(
            modifier = modifier, verticalAlignment = Alignment.CenterVertically
        ) {
            items(items = itemList, key = itemKey) {
                itemContent(it)
            }
        }
    }
}
