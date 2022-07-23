package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R

@Composable
fun TimetablePage() {
    Column {
        TopAppBar(
            title = { Text(text = "Timetable") },
            navigationIcon = {
                Image(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.ic_drawer),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
            },
            actions = {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_lecture_list),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_alarm_default),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
            }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
        }
    }
}

@Preview
@Composable
fun TimetablePagePreview() {
    TimetablePage()
}
