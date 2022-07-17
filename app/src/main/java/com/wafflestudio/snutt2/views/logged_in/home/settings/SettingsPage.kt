package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsPage(navController: NavController) {
    Column {
        Button(onClick = { navController.navigate("appReport") }) { Text(text = "appReport") }
        Button(onClick = { navController.navigate("serviceInfo") }) { Text(text = "serviceInfo") }
        Button(onClick = { navController.navigate("teamInfo") }) { Text(text = "teamInfo") }
        Button(onClick = { navController.navigate("timetableConfig") }) { Text(text = "timetableConfig") }
        Button(onClick = { navController.navigate("userConfig") }) { Text(text = "userConfig") }
    }
}

@Preview
@Composable
fun SettingsPagePreview() {
    val navController = rememberNavController()
    SettingsPage(navController)
}
