package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TutorialPage() {
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current

    val userViewModel = hiltViewModel<UserViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    val handleFacebookSignIn = {
        coroutineScope.launch {
            try {
                apiOnProgress.showProgress(context.getString(R.string.sign_in_sign_in_button))
                val loginResult = facebookLogin(context)
                userViewModel.loginFacebook(
                    loginResult.accessToken.userId,
                    loginResult.accessToken.token
                )
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            } catch (e: Exception) {
                apiOnError(e)
            } finally {
                apiOnProgress.hideProgress()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_in_logo_title),
                modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
            )

            Text(
                text = stringResource(R.string.sign_in_logo_title),
                style = SNUTTTypography.h1,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth(),
                color = SNUTTColors.Gray200,
                onClick = { navController.navigate(NavigationDestination.SignIn) }
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_in_button),
                    style = SNUTTTypography.button,
                )
            }

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth(),
                color = SNUTTColors.Gray200,
                onClick = { navController.navigate(NavigationDestination.SignUp) }
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_up_button),
                    style = SNUTTTypography.button,
                )
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.FacebookBlue,
                onClick = { handleFacebookSignIn() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.iconfacebook),
                        contentDescription = stringResource(id = R.string.sign_in_sign_in_facebook_button),
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 12.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_in_sign_in_facebook_button),
                        color = SNUTTColors.FacebookBlue,
                        style = SNUTTTypography.button
                    )
                }
            }
        }
    }
}

@Composable
fun TutorialPage(modifier: Modifier = Modifier, titleImg: Painter, contentImg: Painter) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(Modifier.weight(3.5f)) {}

        Image(
            modifier = Modifier
                .width(250.dp)
                .height(40.dp),
            painter = titleImg,
            colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            contentDescription = "tutorial title"
        )

        Surface(Modifier.weight(1.0f)) {}

        Image(
            modifier = Modifier
                .height(300.dp),
            painter = contentImg,
            contentDescription = "tutorial contents"
        )

        Surface(Modifier.weight(1.5f)) {}
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    onClickSignIn: () -> Unit,
    onClickSignUp: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        Surface(
            modifier = Modifier
                .background(SNUTTColors.Gray200)
                .fillMaxWidth()
                .height(1.dp)
        ) {}

        Row(modifier = modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SNUTTColors.White900,
                    contentColor = SNUTTColors.Black900
                ),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                onClick = onClickSignIn
            ) {
                Text(
                    text = "로그인",
                    style = MaterialTheme.typography.button
                )
            }

            Surface(
                modifier = Modifier
                    .background(SNUTTColors.Gray200)
                    .fillMaxHeight()
                    .width(1.dp)
            ) {}

            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SNUTTColors.White900,
                    contentColor = SNUTTColors.Black900
                ),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                onClick = onClickSignUp
            ) {
                Text(
                    text = "가입",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TutorialScreen(
    onClickSignIn: () -> Unit,
    onClickSignUp: () -> Unit
) {
    val pagerState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
    ) {
        HorizontalPager(modifier = Modifier.weight(1f), count = 3, state = pagerState) { page ->
            TutorialPage(
                modifier = Modifier.fillMaxWidth(),
                titleImg = painterResource(
                    id = when (page) {
                        0 -> R.drawable.imgintrotitle1
                        1 -> R.drawable.imgintrotitle2
                        else -> R.drawable.imgintrotitle3
                    }
                ),
                contentImg = painterResource(
                    id = when (page) {
                        0 -> R.drawable.imgintro1
                        1 -> R.drawable.imgintro2
                        else -> R.drawable.imgintro3
                    }
                )
            )
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState,
            activeColor = SNUTTColors.Gray200,
            inactiveColor = SNUTTColors.Gray100,
            indicatorHeight = 12.dp,
            indicatorWidth = 12.dp,
        )

        ActionButtons(onClickSignIn = onClickSignIn, onClickSignUp = onClickSignUp)
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
