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
import com.wafflestudio.snutt2.components.TimetableView
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.SettingsRepository
import com.wafflestudio.snutt2.views.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimetableWidgetProvider : AppWidgetProvider() {
    @Inject
    lateinit var myLectureRepository: MyLectureRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

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

        myLectureRepository.lastViewedTable.get().value?.let { table ->
            val tableView = TimetableView(context)

            tableView.theme = table.theme
            tableView.lectures = table.lectureList
            tableView.trimParam = settingsRepository.tableTrimParam.get()

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            tableView.measure(width, height)
            tableView.layout(0, 0, width, height)
            tableView.draw(canvas)

            views.setViewVisibility(R.id.placeholder, View.GONE)
            views.setImageViewBitmap(R.id.table, bitmap)
        }
    }
}
