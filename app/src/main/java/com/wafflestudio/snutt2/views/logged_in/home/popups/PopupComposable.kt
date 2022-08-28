package com.wafflestudio.snutt2.views.logged_in.home.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks

@Composable
fun Popup(url: String, onClickFewDays: () -> Unit, onClickClose: () -> Unit) {
    Box(
        modifier = Modifier
            .zIndex(2f) // TODO: zIndex들도 정리해야
            .fillMaxSize()
            .background(Color(0x80000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(230.dp)
        ) {
            AsyncImage(
                model = url,
                contentDescription = "",
                error = painterResource(id = R.drawable.img_reviews_coming_soon),
//                placeholder = painterResource(id = R.color.white),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.White)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다시 보지 않기",
                    modifier = Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                        .weight(3f)
                        .clicks { onClickFewDays() },
                    textAlign = TextAlign.Center,
                    color = Color.White             // TODO: Color
                )
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(17.dp)
                        .background(Color.White)
                )
                Text(
                    text = "닫기",
                    modifier =
                    Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                        .weight(2f)
                        .clicks { onClickClose() },
                    textAlign = TextAlign.Center,
                    color = Color.White             // TODO: Color
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
