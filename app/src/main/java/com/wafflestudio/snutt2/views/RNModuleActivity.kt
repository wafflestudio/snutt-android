package com.wafflestudio.snutt2.views

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.suspendCoroutine

class RNModuleActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            val urlConnection = URL("https://snutt-rn-assets.s3.ap-northeast-2.amazonaws.com/android.jsbundle").openConnection() as HttpURLConnection
            urlConnection.connect()
            val inputStream = urlConnection.inputStream
            val outputFile = File(applicationContext.cacheDir, "android.jsbundle")

            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024000)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            inputStream.close()
            urlConnection.disconnect()

            val jsBundleFile = File(applicationContext.cacheDir, "android.jsbundle")

            withContext(Dispatchers.Main) {
                val reactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(application)
                    .setCurrentActivity(this@RNModuleActivity)
                    .setJSBundleFile(jsBundleFile.absolutePath)
                    .addPackage(MainReactPackage())
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .build()

                val rootView = ReactRootView(this@RNModuleActivity)
                rootView.startReactApplication(reactInstanceManager, "friends", null)
                setContentView(rootView)
            }
        }

//        val request = DownloadManager.Request(Uri.parse("https://snutt-rn-assets.s3.ap-northeast-2.amazonaws.com/android.jsbundle"))
//            .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "android.jsbundle")
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//
//        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        downloadManager.enqueue(request)
    }
}
