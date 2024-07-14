package com.wafflestudio.snutt2.react_native.event

import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.wafflestudio.snutt2.kakao.buildKakaoAddFriendTemplate
import com.wafflestudio.snutt2.kakao.sendKakaoMessageWithTemplate

@ReactModule(name = RNEventEmitterModule.MODULE_NAME)
class RNEventEmitterModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
    private var registeredListeners: MutableSet<String> = mutableSetOf()
    private val tasks: MutableMap<String, MutableList<() -> Unit>> = mutableMapOf()

    companion object {
        const val MODULE_NAME = "RNEventEmitter"

        private const val REGISTER = "register"
        private const val EVENT_TYPE = "eventType"

        // 카톡친추 관련
        private const val ADD_FRIEND_KAKAO = "add-friend-kakao"
        private const val REQUEST_TOKEN = "requestToken"
        private const val TYPE = "type"
    }

    override fun getName(): String {
        return MODULE_NAME
    }

    @ReactMethod
    fun sendEventToNative(name: String, parameters: ReadableMap?) {
        when (name) {
            ADD_FRIEND_KAKAO -> {
                val requestToken = parameters?.getString(REQUEST_TOKEN) ?: return
                val feedTemplate = buildKakaoAddFriendTemplate(
                    mapOf(
                        REQUEST_TOKEN to requestToken,
                        TYPE to ADD_FRIEND_KAKAO,
                    ),
                )
                if (reactContext.currentActivity != null) {
                    sendKakaoMessageWithTemplate(reactContext.currentActivity as Context, feedTemplate)
                }
            }

            REGISTER -> {
                val eventType = parameters?.getString(EVENT_TYPE) ?: return

                // RN 에서 리스너를 등록했다고 알려 온 eventType 에 대해, 쌓여 있던 task 를 수행한다.
                registeredListeners.add(eventType)
                tasks[eventType]?.forEach { it.invoke() }
                tasks[eventType]?.clear()
            }
        }
    }

    private fun sendEventToReactNative(name: String, parameters: WritableMap?) {
        val isRegisteredEvent = registeredListeners.contains(name)

        val task = {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(name, parameters)
        }

        // RN 쪽에서 이벤트 핸들러를 등록하고 register 이벤트를 쏴 주는데, 이걸 수신했으면 즉시 수행하고 아니면 tasks 에 추가해 뒀다가 나중에 수행한다.
        if (isRegisteredEvent) {
            task.invoke()
        } else {
            tasks.getOrPut(name) { mutableListOf() }.add(task)
        }
    }

    fun sendAddKakaoFriendEvent(requestToken: String) {
        sendEventToReactNative(
            ADD_FRIEND_KAKAO,
            Arguments.createMap().apply {
                putString("requestToken", requestToken)
            },
        )
    }
}
