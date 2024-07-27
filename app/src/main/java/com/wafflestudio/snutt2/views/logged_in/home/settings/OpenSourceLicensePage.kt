package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin

@Composable
fun OpenSourceLicensePage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    var addIdPasswordDialogState by remember { mutableStateOf(false) }
    var passwordChangeDialogState by remember { mutableStateOf(false) }
    var leaveDialogState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_licenses_title),
            onClickNavigateBack = { navController.popBackStack() },
        )

        Margin(height = 10.dp)

        SettingItem(
            title = stringResource(R.string.license_colorpicker_title),
            onClick = { navController.navigate("${NavigationDestination.LicenseDetail}?licenseName=${context.getString(R.string.license_colorpicker_route)}") },
        )

        SettingItem(
            title = stringResource(R.string.license_guava_title),
            onClick = { navController.navigate("${NavigationDestination.LicenseDetail}?licenseName=${context.getString(R.string.license_guava_route)}") },
        )

        SettingItem(
            title = stringResource(R.string.license_retrofit_title),
            onClick = { navController.navigate("${NavigationDestination.LicenseDetail}?licenseName=${context.getString(R.string.license_retrofit_route)}") },
        )

        SettingItem(
            title = stringResource(R.string.license_okhttp_title),
            onClick = { navController.navigate("${NavigationDestination.LicenseDetail}?licenseName=${context.getString(R.string.license_okhttp_route)}") },
        )

        SettingItem(
            title = stringResource(R.string.license_pretendard_title),
            onClick = { navController.navigate("${NavigationDestination.LicenseDetail}?licenseName=${context.getString(R.string.license_pretendard_route)}") },
        )
    }
}
