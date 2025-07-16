package com.wafflestudio.snutt2.lib

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.core.content.FileProvider
import com.facebook.FacebookSdk
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils.displayHeight
import com.wafflestudio.snutt2.SNUTTUtils.displayWidth
import com.wafflestudio.snutt2.components.view.TimetableView
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import java.io.File
import java.io.FileOutputStream

fun shareScreenshot(
    table: TableDto,
    tableTrimParam: TableTrimParam,
    context: Context,
) {
    val view = TimetableView(context)
    view.theme = table.theme
    view.lectures = table.lectureList
    view.trimParam = tableTrimParam

    val width = context.displayWidth.toInt()
    val height = context.displayHeight.toInt()
    view.measure(width, height)
    view.layout(0, 0, width, height)

    val bitmap =
        Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888,
        )
    val canvas = Canvas(bitmap)
    view.draw(canvas)

    val uri = bitmapToUri(bitmap, context)
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
