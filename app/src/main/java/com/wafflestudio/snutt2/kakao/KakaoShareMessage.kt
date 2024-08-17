package com.wafflestudio.snutt2.kakao

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.FeedTemplate
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.toast

fun sendKakaoMessageWithTemplate(context: Context, feedTemplate: FeedTemplate) {
    if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
        ShareClient.instance.shareDefault(context, feedTemplate) { sharingResult, error ->
            if (error != null) {
                context.toast(context.getString(R.string.kakao_friend_share_error))
            } else if (sharingResult != null) {
                context.startActivity(
                    sharingResult.intent.apply {
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                )
            } else {
                context.toast(context.getString(R.string.kakao_friend_share_unknown_error))
            }
        }
    } else {
        val sharerUrl = WebSharerClient.instance.makeDefaultUrl(feedTemplate)
        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        } catch (e: UnsupportedOperationException) {
            context.toast(context.getString(R.string.kakao_friend_share_unsupported_browser))
        }
        try {
            KakaoCustomTabsClient.open(context, sharerUrl)
        } catch (e: ActivityNotFoundException) {
            context.toast(context.getString(R.string.kakao_friend_share_unsupported_browser))
        }
    }
}
