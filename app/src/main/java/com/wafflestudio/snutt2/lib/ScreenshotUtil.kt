package com.wafflestudio.snutt2.lib

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import com.facebook.FacebookSdk
import com.wafflestudio.snutt2.R
import java.io.File
import java.io.FileOutputStream

fun shareScreenshotFromView(
    view: View,
    context: Context,
    topBarHeight: Int,
    bannerHeight: Int,
    timetableHeight: Int,
) {
    val bitmap =
        Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888,
        )
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    // FIXME: 이 방법의 문제점 -> 위아래를 잘라내야 한다.
    val bitmapResized = Bitmap.createBitmap(bitmap, 0, topBarHeight + bannerHeight, bitmap.width, timetableHeight)
    val uri = bitmapToUri(bitmapResized, context)
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/png"
    }
    context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
}

private fun bitmapToUri(image: Bitmap, context: Context): Uri {
    val imagesFolder = File(FacebookSdk.getCacheDir(), "images")
    imagesFolder.mkdirs()
    val file = File(imagesFolder, "shared_image.png")
    val stream = FileOutputStream(file)
    image.compress(Bitmap.CompressFormat.PNG, 90, stream)
    stream.flush()
    stream.close()
    return FileProvider.getUriForFile(
        context,
        context.getString(R.string.file_provider_authorities),
        file,
    )
}
