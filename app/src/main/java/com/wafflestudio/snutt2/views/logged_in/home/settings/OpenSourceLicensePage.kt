package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun OpenSourceLicensePage(
    onNavigateBack: () -> Unit,
    onNavigateLicenseDetail: (String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.settings_licenses_title),
            onClickNavigateBack = onNavigateBack,
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingItem(
            title = stringResource(R.string.license_colorpicker_title),
            onClick = {
                onNavigateLicenseDetail(context.getString(R.string.license_colorpicker_route))
            },
        )

        SettingItem(
            title = stringResource(R.string.license_guava_title),
            onClick = {
                onNavigateLicenseDetail(context.getString(R.string.license_guava_route))
            },
        )

        SettingItem(
            title = stringResource(R.string.license_retrofit_title),
            onClick = {
                onNavigateLicenseDetail(context.getString(R.string.license_retrofit_route))
            },
        )

        SettingItem(
            title = stringResource(R.string.license_okhttp_title),
            onClick = {
                onNavigateLicenseDetail(context.getString(R.string.license_okhttp_route))
            },
        )

        SettingItem(
            title = stringResource(R.string.license_pretendard_title),
            onClick = {
                onNavigateLicenseDetail(context.getString(R.string.license_pretendard_route))
            },
        )
    }
}
