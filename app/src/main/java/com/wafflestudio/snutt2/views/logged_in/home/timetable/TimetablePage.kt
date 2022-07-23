package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerValue
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.views.logged_in.home.HomeDrawer
import com.wafflestudio.snutt2.views.logged_in.home.HomeDrawerStateContext
import com.wafflestudio.snutt2.views.logged_in.home.HomeNavControllerContext
import kotlinx.coroutines.launch

@Composable
fun TimetablePage() {
    val navController = HomeNavControllerContext.current
    val drawerState = HomeDrawerStateContext.current
    val scope = rememberCoroutineScope()
    Column {
        TopAppBar(
            title = { Text(text = "Timetable") },
            navigationIcon = {
                Image(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .clicks {
                            scope.launch { drawerState.open() }
                        },
                    painter = painterResource(id = R.drawable.ic_drawer),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
            },
            actions = {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate("lecturesOfTable") },
                    painter = painterResource(id = R.drawable.ic_lecture_list),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(R.string.home_timetable_drawer)
                )
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate("notification") },
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
    CompositionLocalProvider(
        HomeNavControllerContext provides rememberNavController(),
        HomeDrawerStateContext provides rememberDrawerState(initialValue = DrawerValue.Closed)
    ) {
        TimetablePage()
    }
}
