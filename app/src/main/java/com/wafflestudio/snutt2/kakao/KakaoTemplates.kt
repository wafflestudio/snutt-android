package com.wafflestudio.snutt2.kakao

import android.content.Context
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.wafflestudio.snutt2.R

val buildKakaoAddFriendTemplate: (context: Context, parameter: Map<String, String>) -> FeedTemplate = { context, parameter ->
    FeedTemplate(
        content = Content(
            title = context.getString(R.string.kakao_friend_template_title),
            description = context.getString(R.string.kakao_friend_template_description),
            imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/PurpleSource122/v4/f0/c6/58/f0c6581d-dd41-3bad-9d9a-516561d35af1/0d1dfc21-5d2e-4dcf-8cff-c6eb25fe7284_2_2.png/460x0w.webp",
            link = Link(
                androidExecutionParams = parameter,
                iosExecutionParams = parameter,
            ),
        ),
        buttons = listOf(
            Button(
                context.getString(R.string.kakao_friend_template_button),
                Link(
                    androidExecutionParams = parameter,
                    iosExecutionParams = parameter,
                ),
            ),
        ),
    )
}
