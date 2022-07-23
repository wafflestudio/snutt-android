package com.wafflestudio.snutt2.views.logged_in.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewPage
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchPage
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingsPage
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetablePage
import java.lang.RuntimeException

enum class HomeItem(@DrawableRes val icon: Int) {
    Timetable(R.drawable.ic_timetable),
    Search(R.drawable.ic_search),
    Review(R.drawable.ic_review),
    Settings(R.drawable.ic_setting)
}

val HomeDrawerStateContext = compositionLocalOf<DrawerState> {
    throw RuntimeException("")
}

val HomeNavControllerContext = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

@Composable
fun HomePage(navController: NavController) {
    var currentScreen by remember { mutableStateOf(HomeItem.Timetable) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalDrawer(
        drawerContent = { HomeDrawer() },
        drawerState = drawerState,
        gesturesEnabled = currentScreen == HomeItem.Timetable
    ) {
        CompositionLocalProvider(
            HomeDrawerStateContext provides drawerState,
            HomeNavControllerContext provides navController
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (currentScreen) {
                        HomeItem.Timetable -> TimetablePage()
                        HomeItem.Search -> SearchPage()
                        HomeItem.Review -> ReviewPage()
                        HomeItem.Settings -> SettingsPage()
                    }
                }

                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Timetable },
                    ) {
                        Text(text = "timetable")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Search },
                    ) {
                        Text(text = "search")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Review },
                    ) {
                        Text(text = "review")
                    }

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { currentScreen = HomeItem.Settings },
                    ) {
                        Text(text = "settings")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage(rememberNavController())
}
