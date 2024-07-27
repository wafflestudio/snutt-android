package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun LicenseDetailPage() {
    val navController = LocalNavController.current

    val licenseName = navController.currentBackStackEntry?.arguments?.getString("licenseName")

    val title: String
    val name: String
    val content: String

    when (licenseName) {
        stringResource(R.string.license_colorpicker_route) -> {
            title = stringResource(R.string.license_colorpicker_title)
            name = stringResource(R.string.license_colorpicker_name)
            content = stringResource(R.string.license_colorpicker_content)
        }
        stringResource(R.string.license_guava_route) -> {
            title = stringResource(R.string.license_guava_title)
            name = stringResource(R.string.license_guava_name)
            content = stringResource(R.string.license_guava_content)
        }
        stringResource(R.string.license_retrofit_route) -> {
            title = stringResource(R.string.license_retrofit_title)
            name = stringResource(R.string.license_retrofit_name)
            content = stringResource(R.string.license_retrofit_content)
        }
        stringResource(R.string.license_okhttp_route) -> {
            title = stringResource(R.string.license_okhttp_title)
            name = stringResource(R.string.license_okhttp_name)
            content = stringResource(R.string.license_okhttp_content)
        }
        stringResource(R.string.license_pretendard_route) -> {
            title = stringResource(R.string.license_pretendard_title)
            name = stringResource(R.string.license_pretendard_name)
            content = stringResource(R.string.license_pretendard_content)
        }
        else -> {
            title = ""
            name = ""
            content = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        SimpleTopBar(
            title = title,
            onClickNavigateBack = { navController.popBackStack() },
        )

        Text(
            text = name,
            style = SNUTTTypography.body2,
            modifier = Modifier.padding(15.dp),
        )

        Text(
            text = content,
            style = SNUTTTypography.body2,
            modifier = Modifier.padding(15.dp),
        )
    }
}
