package com.wafflestudio.snutt2.lib.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SnuttTypography

@Composable
fun TopBar(onButtonClick: () -> Unit, titleText: Int) {
    Surface(elevation = 2.dp) {
        Row(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "back button",
                modifier = Modifier
                    .size(30.dp)
                    .clickableWithoutRippleEffect(
                        onClick = onButtonClick
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(titleText), style = SnuttTypography.h2)
        }
    }
}

fun Modifier.clickableWithoutRippleEffect(onClick: () -> Unit): Modifier =
    clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )
