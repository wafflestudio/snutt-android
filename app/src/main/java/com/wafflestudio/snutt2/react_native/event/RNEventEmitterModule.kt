package com.wafflestudio.snutt2.react_native.event

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = RNEventEmitterModule.MODULE_NAME)
class RNEventEmitterModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val MODULE_NAME = "RNEventEmitter"
    }

    override fun getName(): String {
        return MODULE_NAME
    }
}
