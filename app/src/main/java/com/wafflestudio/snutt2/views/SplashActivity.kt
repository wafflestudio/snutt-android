package com.wafflestudio.snutt2.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.ui.SnuttTheme
import com.wafflestudio.snutt2.ui.White900
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by makesource on 2017. 4. 1..
 */

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .background(White900)
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "logo")
        Text(
            modifier = Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.h1,
            text = "SNUTT",
        )
    }
}

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    SnuttTheme {
                        SplashScreen()
                    }
                }
            }
        )
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this, RootActivity::class.java))
                finish()
            },
            500L
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SnuttTheme {
        SplashScreen()
    }
}
