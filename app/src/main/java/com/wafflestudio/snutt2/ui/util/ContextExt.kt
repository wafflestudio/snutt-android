package com.wafflestudio.snutt2.ui.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.wafflestudio.snutt2.R
import java.io.File

fun Context.toast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT,
    ).show()
}

/**
 * 앱이 내려받은 파일을 FileProvider content URI 로 감싸 외부 뷰어 앱(PDF 뷰어 등)으로 연다.
 * 열 수 있는 앱이 없으면 false 를 반환한다.
 */
fun Context.openFileInViewer(file: File, mimeType: String?): Boolean {
    val uri = FileProvider.getUriForFile(
        this,
        getString(R.string.file_provider_authorities),
        file,
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType ?: "*/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // applicationContext 에서 호출될 수 있으므로 새 태스크로 띄운다.
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return runCatching { startActivity(intent) }.isSuccess
}
