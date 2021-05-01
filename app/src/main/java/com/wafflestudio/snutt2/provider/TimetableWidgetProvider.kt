package com.wafflestudio.snutt2.provider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.RemoteViews
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils.displayHeight
import com.wafflestudio.snutt2.SNUTTUtils.displayWidth
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.ui.SplashActivity
import com.wafflestudio.snutt2.view.TableView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by makesource on 2017. 8. 19..
 */
@AndroidEntryPoint
class TimetableWidgetProvider : AppWidgetProvider() {
    @Inject
    lateinit var prefStorage: PrefStorage

    @Inject
    lateinit var lectureManager: LectureManager

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, SplashActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val views = RemoteViews(context.packageName, R.layout.widget_timetable)
            views.setOnClickPendingIntent(R.id.layout, pendingIntent)
            renderViews(context, appWidgetManager, appWidgetId, views)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun renderViews(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews
    ) {
        val width = context.displayWidth.toInt()
        val height = context.displayHeight.toInt()
        if (prefStorage.currentTable != null) {
            val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)
            val table = TableView(context)
            table.drawWidget(canvas, width, height)
            views.setViewVisibility(R.id.placeholder, View.GONE)
            views.setImageViewBitmap(R.id.table, resultBitmap)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
    }
}
