package com.wafflestudio.snutt2.react_native

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.reactnativecommunity.picker.RNCPickerPackage
import com.swmansion.gesturehandler.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.ui.isSystemDarkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ReactNativeBundleManager(
    private val context: Context,
    private val remoteConfig: RemoteConfig,
    private val token: StateFlow<String>,
    private val themeMode: StateFlow<ThemeMode>,
) {
    private val rnBundleFileSrc: String
        get() = if (USE_LOCAL_BUNDLE) LOCAL_BUNDLE_URL else remoteConfig.friendBundleSrc
    private var myReactInstanceManager: ReactInstanceManager? = null
    var reactRootView: ReactRootView? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            remoteConfig.waitForFetchConfig()
            token.filter { it.isNotEmpty() }.collectLatest {
                val jsBundleFile = getExistingFriendsBundleFileOrNull() ?: return@collectLatest
                withContext(Dispatchers.Main) {
                    myReactInstanceManager = ReactInstanceManager.builder()
                        .setApplication(context.applicationContext as Application)
                        .setCurrentActivity(context as Activity)
                        .setJavaScriptExecutorFactory(HermesExecutorFactory())
                        .setJSBundleFile(jsBundleFile.absolutePath)
                        .addPackages(
                            listOf(MainReactPackage(), RNGestureHandlerPackage(), ReanimatedPackage(), SafeAreaContextPackage(), RNCPickerPackage(), SvgPackage()),
                        )
                        .setInitialLifecycleState(LifecycleState.RESUMED)
                        .build()
                    themeMode.collectLatest {
                        val isDarkMode = when (it) {
                            ThemeMode.AUTO -> isSystemDarkMode(context)
                            else -> it == ThemeMode.DARK
                        }
                        reactRootView = ReactRootView(context).apply {
                            startReactApplication(
                                myReactInstanceManager!!,
                                FRIENDS_MODULE_NAME,
                                Bundle().apply {
                                    putString("x-access-token", token.value)
                                    putString("x-access-apikey", context.getString(R.string.api_key))
                                    putString("theme", if (isDarkMode) "dark" else "light")
                                    putBoolean("allowFontScaling", true)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // 번들 파일을 저장할 폴더 (없으면 만들기, 실패하면 null)
    private fun bundlesBaseFolder(): File? {
        val baseDir = File(context.applicationContext.dataDir.absolutePath, BUNDLE_BASE_FOLDER)
        return if (baseDir.isDirectory && baseDir.exists()) {
            baseDir
        } else if (baseDir.mkdir()) {
            baseDir
        } else {
            null
        }
    }

    private fun getExistingFriendsBundleFileOrNull(): File? {
        val baseDir = bundlesBaseFolder() ?: return null
        val friendsBaseDir = File(baseDir, FRIENDS_MODULE_NAME)
        if (friendsBaseDir.exists().not() && friendsBaseDir.mkdir().not()) return null

        // Config에서 가져온 bundle name대로 fileName을 만든다.
        val targetFileName =
            if (USE_LOCAL_BUNDLE) {
                LOCAL_BUNDLE_FILE_NAME
            } else {
                Regex(BUNDLE_FILE_NAME_REGEX).find(rnBundleFileSrc)?.groupValues?.get(1)?.plus(BUNDLE_FILE_SUFFIX) ?: return null
            }
        val targetFile = File(friendsBaseDir, targetFileName)

        // 최신 friends 번들 외에 전부 삭제
        friendsBaseDir.listFiles()
            ?.filter { it.name != targetFileName }
            ?.forEach { it.delete() }

        // 파일이 없거나 파일에 문제가 있으면 새로 다운로드한다.
        if (targetFile.exists().not() || targetFile.canRead().not() || USE_LOCAL_BUNDLE) { // TODO: 올바르지 않은 bundle 파일인지 더 정확히 판단하기
            try {
                val urlConnection = URL(rnBundleFileSrc).openConnection() as HttpURLConnection
                urlConnection.connect()
                val inputStream = urlConnection.inputStream
                val outputStream = FileOutputStream(targetFile)
                val buffer = ByteArray(1024000)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()
                urlConnection.disconnect()
            } catch (e: Exception) {
                return null
            }
        }
        return targetFile
    }

    // 번들 파일들은 $rootDir/data/ReactNativeBundles 폴더에 각 모듈별로 저장된다.
    // friends 모듈의 번들 파일은 $rootDir/data/ReactNativeBundles/friends 폴더에 저장된다.
    // 번들 파일의 이름은 src가 https://~~~.com/{version}/android.jsbundle 일 때 version-android.jsbundle 이다.
    companion object {
        const val BUNDLE_BASE_FOLDER = "/ReactNativeBundles"
        const val BUNDLE_FILE_NAME_REGEX = "com/(.*?)/android.jsbundle"
        const val BUNDLE_FILE_SUFFIX = "-android.jsbundle"

        const val FRIENDS_MODULE_NAME = "friends"

        const val USE_LOCAL_BUNDLE = false
        const val LOCAL_BUNDLE_FILE_NAME = "android.jsbundle"
        const val LOCAL_BUNDLE_URL = "http://localhost:8081/index.bundle?platform=android"
    }
}
