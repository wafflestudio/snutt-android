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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.ArrowDownIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.NetworkLog
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun NetworkLogPage() {
    val vm: DebugViewModel = hiltViewModel()
    val navController = LocalNavController.current

    val logList by vm.networkLog.collectAsState()

    Column {
        TopBar(
            title = {
                Text("네트워크 로그", style = SNUTTTypography.h2)
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier.clicks { navController.popBackStack() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
            actions = {
                Text(
                    "지우기", style = SNUTTTypography.button,
                    modifier = Modifier.clicks {
                        vm.clearNetworkLog()
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
                else -> SNUTTColors.DarkGray
            },
        )
    }
    Text(
        text = log.requestUrl,
        style = SNUTTTypography.h4,
        modifier = Modifier.clicks { expanded = expanded.not() },
        overflow = if (expanded.not()) TextOverflow.Ellipsis else TextOverflow.Visible,
        maxLines = if (expanded.not()) 1 else Int.MAX_VALUE,
    )
    SimpleTextToggle(title = "Request Header", content = log.requestHeader)
    SimpleTextToggle(title = "Request Body", content = log.requestBody)
    SimpleTextToggle(title = "Response Body", content = log.responseBody)
}

@Composable
private fun SimpleTextToggle(
    title: String,
    content: String,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 0f else -90f)

    Row(
        modifier = Modifier
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
        }
    }
}
