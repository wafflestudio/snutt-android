package com.wafflestudio.snutt2.kakao

import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link

val buildKakaoAddFriendTemplate: (parameter: Map<String, String>) -> FeedTemplate = { parameter ->
    FeedTemplate(
        content = Content(
            title = "SNUTT : 서울대학교 시간표 앱",
            description = "스누티티 친구 초대가 도착했어요",
            imageUrl = "https://is1-ssl.mzstatic.com/image/thumb/PurpleSource122/v4/f0/c6/58/f0c6581d-dd41-3bad-9d9a-516561d35af1/0d1dfc21-5d2e-4dcf-8cff-c6eb25fe7284_2_2.png/460x0w.webp",
            link = Link(
                androidExecutionParams = parameter,
                iosExecutionParams = parameter,
            ),
        ),
        buttons = listOf(
            Button(
                "앱 실행하기",
                Link(
                    androidExecutionParams = parameter,
                    iosExecutionParams = parameter,
                ),
            ),
        ),
    )
}
