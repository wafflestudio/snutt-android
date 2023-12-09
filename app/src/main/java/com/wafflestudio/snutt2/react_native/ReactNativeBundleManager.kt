package com.wafflestudio.snutt2.react_native

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage
import com.reactnativecommunity.picker.RNCPickerPackage
import com.swmansion.gesturehandler.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.NetworkConnectivityManager
import com.wafflestudio.snutt2.ui.isDarkMode
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@ActivityScoped
class ReactNativeBundleManager @Inject constructor(
    @ApplicationContext applicationContext: Context,
    @ActivityContext activityContext: Context,
    remoteConfig: RemoteConfig,
    userRepository: UserRepository,
    networkConnectivityManager: NetworkConnectivityManager,
) {
    private var myReactInstanceManager: ReactInstanceManager? = null
    var reactRootView = mutableStateOf<ReactRootView?>(null)
    private val reloadSignal = MutableSharedFlow<Unit>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(
                if (USE_LOCAL_BUNDLE) MutableStateFlow(LOCAL_BUNDLE_URL) else remoteConfig.friendsBundleSrc,
                userRepository.accessToken.filter { it.isNotEmpty() },
                userRepository.themeMode,
                networkConnectivityManager.networkConnectivity.filter { it },
                reloadSignal.onStart { emit(Unit) },
            ) { bundleSrc, token, theme, _, _ ->
                getExistingBundleFileOrNull(applicationContext, bundleSrc)?.let { bundleFile ->
                    withContext(Dispatchers.Main) {
                        if (myReactInstanceManager == null) {
                            myReactInstanceManager = ReactInstanceManager.builder()
                                .setApplication(applicationContext as Application)
                                .setCurrentActivity(activityContext as Activity)
                                .setJavaScriptExecutorFactory(HermesExecutorFactory())
                                .setJSBundleFile(bundleFile.absolutePath)
                                .addPackages(
                                    listOf(MainReactPackage(), RNGestureHandlerPackage(), ReanimatedPackage(), SafeAreaContextPackage(), RNCPickerPackage(), SvgPackage(), AsyncStoragePackage()),
                                )
                                .setInitialLifecycleState(LifecycleState.RESUMED)
                                .build()
                        }

                        reactRootView.value = ReactRootView(activityContext).apply {
                            startReactApplication(
                                myReactInstanceManager ?: return@apply,
                                FRIENDS_MODULE_NAME,
                                Bundle().apply {
                                    putString("x-access-token", token)
                                    putString("x-access-apikey", context.getString(R.string.api_key))
                                    putString("theme", if (isDarkMode(activityContext, theme)) "dark" else "light")
                                    putBoolean("allowFontScaling", true)
                                },
                            )
                        }
                    }
                }
            }.collect()
        }
    }

    // 번들 파일을 저장할 폴더 (없으면 만들기, 실패하면 null)
    private fun bundlesBaseFolder(context: Context): File? {
        val baseDir = File(context.dataDir.absolutePath, BUNDLE_BASE_FOLDER)
        return if (baseDir.isDirectory && baseDir.exists()) {
            baseDir
        } else if (baseDir.mkdir()) {
            baseDir
        } else {
            null
        }
    }

    private fun getExistingBundleFileOrNull(
        context: Context,
        rnBundleFileSrc: String,
        moduleName: String = FRIENDS_MODULE_NAME, // 나중에 다른 모듈 추가되면 이 파라미터 사용
    ): File? {
        val baseDir = bundlesBaseFolder(context) ?: return null
        val friendsBaseDir = File(baseDir, moduleName)
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

    // 수동으로 번들 reload하고 싶을 때 사용
    suspend fun reloadBundle() {
        reloadSignal.emit(Unit)
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
