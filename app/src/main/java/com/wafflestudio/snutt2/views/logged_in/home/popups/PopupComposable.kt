package com.wafflestudio.snutt2.views.logged_in.home.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun Popup(url: String, onClickFewDays: () -> Unit, onClickClose: () -> Unit) {
    val imageWidth = (LocalConfiguration.current.screenWidthDp * 0.8).dp
    Box(
        modifier = Modifier
            .zIndex(2f)
            .fillMaxSize()
            .background(SNUTTColors.Dim2)
            .clicks {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(imageWidth)
        ) {
            AsyncImage(
                model = url,
                contentDescription = "",
                error = painterResource(id = R.drawable.img_reviews_coming_soon),
                contentScale = ContentScale.FillWidth,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.popup_hide_message),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(3f)
                        .clicks { onClickFewDays() },
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = SNUTTColors.AllWhite
                )
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(17.dp)
                        .background(Color.White)
                )
                Text(
                    text = stringResource(id = R.string.popup_close_message),
                    modifier =
                    Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                        .weight(2f)
                        .clicks { onClickClose() },
                    textAlign = TextAlign.Center,
                    color = SNUTTColors.AllWhite
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PopupPreview() {
    Popup(url = "", {}, {})
}
