package com.wafflestudio.snutt2.react_native

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.ReactContext
import com.wafflestudio.snutt2.react_native.event.RNEventEmitterModule

class SNUTTReactRootView : ReactRootView {
    private var attached = false
    private val callbacks: MutableList<(ReactContext) -> Unit> = mutableListOf()

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, reactInstanceManager: ReactInstanceManager) : super(context) {
        reactInstanceManager.addReactInstanceEventListener { reactContext ->
            attached = true
            callbacks.forEach { it.invoke(reactContext) }
            callbacks.clear()
        }
        parseIntent()
    }

    private fun subscribeAttach(callback: (ReactContext) -> Unit) {
        if (attached) {
            callback.invoke(context as ReactContext)
        } else {
            callbacks.add(callback)
        }
    }

    private fun parseIntent() {
        // 카톡 링크를 눌러 진입했을 경우, requestToken 이 intent.data 에 쿼리로 들어 있다.
        // 고민 : intent 처리를 여기서 하는 게 맞는 걸까? RootActivity 에서 해 주는 것과, 여기서 해 주는 것으로 파편화되어버렸다
        val requestToken = (context as Activity).intent.data?.getQueryParameter("requestToken")
        requestToken?.let {
            sendAddKakaoFriendEvent(it)
        }
    }

    private fun sendAddKakaoFriendEvent(requestToken: String) {
        subscribeAttach { reactContext ->
            val eventEmitterModule = reactContext.getNativeModule(RNEventEmitterModule::class.java) ?: return@subscribeAttach
            requestToken.let { eventEmitterModule.sendAddKakaoFriendEvent(it) }
        }
    }
}
