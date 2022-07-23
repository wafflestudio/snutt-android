package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.wafflestudio.snutt2.views.logged_in.home.HomeNavControllerContext

@Composable
fun SettingsPage() {
    val navController = HomeNavControllerContext.current
    val viewModel = hiltViewModel<SettingsViewModel>()

    Column {
        Button(onClick = { navController.navigate("appReport") }) { Text(text = "appReport") }
        Button(onClick = { navController.navigate("serviceInfo") }) { Text(text = "serviceInfo") }
        Button(onClick = { navController.navigate("teamInfo") }) { Text(text = "teamInfo") }
        Button(onClick = { navController.navigate("timetableConfig") }) { Text(text = "timetableConfig") }
        Button(onClick = { navController.navigate("userConfig") }) { Text(text = "userConfig") }
        Text(text = "${viewModel.trimParam.get()}")
    }
}

@Preview
@Composable
fun SettingsPagePreview() {
    CompositionLocalProvider(
        HomeNavControllerContext provides rememberNavController()
    ) {
        SettingsPage()
    }
}
