package com.wafflestudio.snutt2.feature.syllabus

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.util.openFileInViewer
import com.wafflestudio.snutt2.ui.util.toast
import java.io.File

/**
 * WebView 세션 안에서 트리거된 강의계획서 첨부 파일(PDF 등)을 앱 전용 캐시로 내려받는다.
 *
 * WebView 다운로드는 브라우징 세션의 연속이므로, 시스템 다운로드 서비스인 DownloadManager 에
 * WebView 세션의 Referer·쿠키·User-Agent 를 그대로 실어 위임한다. 저장 위치는 앱 전용 외부 캐시라
 * 저장소 권한이 필요 없고 저장공간이 부족하면 시스템이 자동 정리한다.
 * 다운로드가 끝나면 뷰어 앱으로 연다.
 */
object SyllabusDownloader {

    private const val CACHE_SUBDIR = "syllabus"

    fun enqueue(
        context: Context,
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        referer: String,
    ) {
        val appContext = context.applicationContext
        val cacheDir = appContext.externalCacheDir ?: return
        val downloadDir = File(cacheDir, CACHE_SUBDIR).apply { mkdirs() }
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
        val destFile = File(downloadDir, fileName)
        // 같은 파일명이 있으면 덮어쓴다(중복 파일 누적 방지).
        if (destFile.exists()) destFile.delete()

        val request = DownloadManager.Request(url.toUri()).apply {
            addRequestHeader("Referer", referer)
            CookieManager.getInstance().getCookie(url)?.let { addRequestHeader("Cookie", it) }
            userAgent?.let { addRequestHeader("User-Agent", it) }
            setMimeType(mimeType)
            setTitle(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationUri(Uri.fromFile(destFile))
        }
        val downloadManager = appContext.getSystemService(DownloadManager::class.java)
        val downloadId = downloadManager.enqueue(request)
        registerCompletionReceiver(appContext, downloadManager, downloadId, mimeType)
    }

    private fun registerCompletionReceiver(
        appContext: Context,
        downloadManager: DownloadManager,
        downloadId: Long,
        mimeType: String?,
    ) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val completedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (completedId != downloadId) return
                appContext.unregisterReceiver(this)
                openDownloadedFile(appContext, downloadManager, downloadId, mimeType)
            }
        }
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    private fun openDownloadedFile(
        appContext: Context,
        downloadManager: DownloadManager,
        downloadId: Long,
        mimeType: String?,
    ) {
        val file = queryDownloadedFile(downloadManager, downloadId) ?: return
        if (!appContext.openFileInViewer(file, mimeType)) {
            appContext.toast(appContext.getString(R.string.syllabus_download_open_failed))
        }
    }

    private fun queryDownloadedFile(downloadManager: DownloadManager, downloadId: Long): File? {
        downloadManager.query(DownloadManager.Query().setFilterById(downloadId)).use { cursor ->
            if (!cursor.moveToFirst()) return null
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            if (cursor.getInt(statusIndex) != DownloadManager.STATUS_SUCCESSFUL) return null
            val localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val localUri = cursor.getString(localUriIndex) ?: return null
            val path = localUri.toUri().path ?: return null
            return File(path)
        }
    }
}
