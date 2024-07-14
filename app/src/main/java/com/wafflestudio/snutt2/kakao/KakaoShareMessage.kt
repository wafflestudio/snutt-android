package com.wafflestudio.snutt2.kakao

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.FeedTemplate

fun sendKakaoMessageWithTemplate(context: Context, feedTemplate: FeedTemplate) {
    if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
        ShareClient.instance.shareDefault(context, feedTemplate) { sharingResult, error ->
            if (error != null) {
                Toast.makeText(context, "카카오톡 공유에 실패했습니다", Toast.LENGTH_SHORT).show()
            } else if (sharingResult != null) {
                context.startActivity(
                    sharingResult.intent.apply {
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            }
        }
    } else {
        val sharerUrl = WebSharerClient.instance.makeDefaultUrl(feedTemplate)
        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        } catch (e: UnsupportedOperationException) {
            // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
        }
        try {
            KakaoCustomTabsClient.open(context, sharerUrl)
        } catch (e: ActivityNotFoundException) {
            // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
        }
    }
}
