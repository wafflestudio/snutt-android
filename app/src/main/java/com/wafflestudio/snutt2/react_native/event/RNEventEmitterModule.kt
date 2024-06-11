package com.wafflestudio.snutt2.react_native.event

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class RNEventEmitterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val MODULE_NAME = "RNEventEmitter"
    }

    override fun getName(): String {
        return MODULE_NAME
    }

    @ReactMethod
    fun sendEventToNative(name: String, parameters: ReadableMap?) {
        Log.d("aaaa", "$name $parameters")
    }
}
