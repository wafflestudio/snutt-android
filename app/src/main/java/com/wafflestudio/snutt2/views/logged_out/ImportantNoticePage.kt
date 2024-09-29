package com.wafflestudio.snutt2.views.logged_out

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.model.ImportantNotice
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun ImportantNoticePage(){
    val importantNoticeViewModel = hiltViewModel<ImportantNoticeViewModel>()
    val importantNotice = importantNoticeViewModel.importantNotice.collectAsState()

    LaunchedEffect(Unit) {
        importantNoticeViewModel.getConfigs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_cat_retry),
            contentDescription = stringResource(R.string.sign_in_logo_title),
            modifier = Modifier.width(30.dp),
        )
    }
}
