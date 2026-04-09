package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.ui.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.ui.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.lib.copyToClipboard
import com.wafflestudio.snutt2.lib.android.NetworkLog
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTColors.SettingBackground
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun NetworkLogPage(
    onNavigateBack: () -> Unit,
    viewModel: DebugViewModel = hiltViewModel(),
) {
    val logList by viewModel.networkLog.collectAsState()

    Column {
        TopBar(
            title = {
                Text(stringResource(R.string.debug_network_log_title), style = SNUTTTypography.h2)
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier.clicks { onNavigateBack() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
            actions = {
                Text(
                    stringResource(R.string.debug_network_log_clear), style = SNUTTTypography.button,
                    modifier = Modifier.clicks {
                        viewModel.clearNetworkLog()
                    },
                )
            },
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            items(logList) {
                NetworkLogItem(it)
                Divider(color = SNUTTColors.Black250)
            }
        }
    }
}

@Composable
private fun NetworkLogItem(log: NetworkLog) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = log.requestMethod, style = SNUTTTypography.h3)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = log.responseCode,
                style = SNUTTTypography.body1,
                color = when (log.responseCode.first()) {
                    '4' -> SNUTTColors.Red
                    '5' -> SNUTTColors.Orange
                    '2' -> SNUTTColors.Grass
                    else -> MaterialTheme.colors.SettingBackground
                },
            )
        }
        Row {
            Text(
                text = log.requestUrl,
                style = SNUTTTypography.h4,
                modifier = Modifier
                    .weight(1f)
                    .clicks { expanded = expanded.not() },
                overflow = if (expanded.not()) TextOverflow.Ellipsis else TextOverflow.Visible,
                maxLines = if (expanded.not()) 1 else Int.MAX_VALUE,
            )
            DuplicateIcon(
                modifier = Modifier
                    .size(20.dp)
                    .clicks {
                        copyToClipboard(
                            context = context,
                            content = log.requestUrl,
                        )
                    },
            )
        }
        SimpleTextToggle(title = "Request Header", content = log.requestHeader)
        SimpleTextToggle(title = "Request Body", content = log.requestBody)
        SimpleTextToggle(title = "Response Body", content = log.responseBody)
    }
}

@Composable
private fun SimpleTextToggle(
    title: String,
    content: String,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 0f else -90f)
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clicks { expanded = expanded.not() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = SNUTTTypography.subtitle1)
        ArrowDownIcon(
            modifier = Modifier
                .size(15.dp)
                .rotate(rotation),
        )
    }
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SNUTTColors.Gray100, shape = RoundedCornerShape(10.dp))
                .padding(10.dp),
        ) {
            Text(text = content, style = SNUTTTypography.body1)
            DuplicateIcon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .clicks {
                        copyToClipboard(
                            context = context,
                            content = content,
                        )
                    },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkLogItemPreview() {
    NetworkLogItem(
        NetworkLog(
            requestMethod = "GET",
            requestUrl = "https://example.com",
            requestHeader = "header",
            requestBody = "body",
            responseCode = "200",
            responseBody = "response",
        ),
    )
}
