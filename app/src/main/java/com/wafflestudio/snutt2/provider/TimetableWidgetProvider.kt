package com.wafflestudio.snutt2.provider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.RemoteViews
import androidx.compose.animation.ExperimentalAnimationApi
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils.displayHeight
import com.wafflestudio.snutt2.SNUTTUtils.displayWidth
import com.wafflestudio.snutt2.components.view.TimetableView
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.views.RootActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalAnimationApi
class
TimetableWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var currentLectureRepository: CurrentTableRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, RootActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val views = RemoteViews(context.packageName, R.layout.widget_timetable)
            views.setOnClickPendingIntent(R.id.layout, pendingIntent)

            // render views
            val compactMode = userRepository.compactMode.value
            val width = context.displayWidth.toInt()
            val height = context.displayHeight.toInt()
            views.setViewVisibility(R.id.placeholder, View.VISIBLE)
            views.setViewVisibility(R.id.table, View.GONE)
            currentLectureRepository.currentTable.value?.let { table ->
                val tableView = TimetableView(context, compactMode)

                tableView.theme = table.theme
                tableView.lectures = table.lectureList
                tableView.trimParam = userRepository.tableTrimParam.value

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)

                tableView.measure(width, height)
                tableView.layout(0, 0, width, height)
                tableView.draw(canvas)

                views.setViewVisibility(R.id.placeholder, View.GONE)
                views.setViewVisibility(R.id.table, View.VISIBLE)
                views.setImageViewBitmap(R.id.table, bitmap)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun refreshWidget(context: Context) {
            val intent = Intent(context, TimetableWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids: IntArray =
                AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(
                        ComponentName(
                            context,
                            TimetableWidgetProvider::class.java,
                        ),
                    )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}
