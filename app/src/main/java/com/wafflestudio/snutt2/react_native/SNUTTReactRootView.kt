package com.wafflestudio.snutt2.react_native

import android.content.Context
import android.util.AttributeSet
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.ReactContext

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
    }

    private fun subscribeAttach(callback: (ReactContext) -> Unit) {
        if (attached) {
            callback.invoke(context as ReactContext)
        } else {
            callbacks.add(callback)
        }
    }
}
