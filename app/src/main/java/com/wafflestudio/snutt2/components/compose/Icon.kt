package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R

@Composable
fun DrawerIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_drawer),
        contentDescription = ""
    )
}

@Composable
fun ListIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_lecture_list),
        contentDescription = ""
    )
}

@Composable
fun NotificationIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_alarm_default),
        contentDescription = ""
    )
}

@Composable
fun ShareIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_share),
        contentDescription = ""
    )
}

@Composable
fun ArrowBackIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_arrow_back),
        contentDescription = ""
    )
}

@Composable
fun SearchIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_search_unselected),
        contentDescription = ""
    )
}

@Composable
fun FilterIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_filter),
        contentDescription = ""
    )
}

@Composable
fun ExitIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(30.dp),
        painter = painterResource(id = R.drawable.ic_exit),
        contentDescription = ""
    )
}

@Composable
fun TagIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(15.dp),
        painter = painterResource(id = R.drawable.ic_tag),
        contentDescription = ""
    )
}

@Composable
fun ClockIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(15.dp),
        painter = painterResource(id = R.drawable.ic_clock),
        contentDescription = ""
    )
}

@Composable
fun LocationIcon(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier.size(15.dp),
        painter = painterResource(id = R.drawable.ic_location),
        contentDescription = ""
    )
}
