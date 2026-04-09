package com.wafflestudio.snutt2.provider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import androidx.compose.animation.ExperimentalAnimationApi
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.SNUTTUtils.displayHeight
import com.wafflestudio.snutt2.lib.SNUTTUtils.displayWidth
import com.wafflestudio.snutt2.ui.components.view.TimetableView
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.domain.ThemeService
import com.wafflestudio.snutt2.views.RootActivity
import kotlinx.coroutines.runBlocking
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalAnimationApi
class
TimetableWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var tableRepository: TableRepository

    @Inject
    lateinit var tableDisplayRepository: TableDisplayRepository

    @Inject
    lateinit var themeService: ThemeService

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = createResponsiveRemoteViews(context, appWidgetManager, appWidgetId)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        // render views
        val views = createResponsiveRemoteViews(context, appWidgetManager, appWidgetId)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    private fun createResponsiveRemoteViews(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int): RemoteViews {
        val dm = context.resources.displayMetrics
        val options = appWidgetManager.getAppWidgetOptions(appWidgetId)

        val sizes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            options.getParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES, SizeF::class.java)
        } else {
            @Suppress("DEPRECATION")
            options.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
        }
        val maxBitmapSize = context.displayWidth * context.displayHeight * 1.5
        var scale = 1.0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !sizes.isNullOrEmpty()) {
            val totalBitmapSize = sizes.sumOf { size -> (size.width * dm.density * size.height * dm.density).toInt() }
            while (totalBitmapSize / (scale * scale) > maxBitmapSize) {
                scale++
            }

            return RemoteViews(
                sizes.associateWith {
                    createRemoteViews(context, (it.width * dm.density).toInt(), (it.height * dm.density).toInt(), scale)
                },
            )
        } else {
            // Before API Level 31
            val minWidth = (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) * dm.density).toInt()
            val maxWidth = (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH) * dm.density).toInt()
            val minHeight = (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) * dm.density).toInt()
            val maxHeight = (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT) * dm.density).toInt()

            if (minWidth == maxWidth && minHeight == maxHeight) {
                return createRemoteViews(context, minWidth, minHeight)
            } else {
                val totalBitmapSize = maxWidth * minHeight + minWidth * maxHeight
                while (totalBitmapSize / (scale * scale) > maxBitmapSize) {
                    scale++
                }

                return RemoteViews(createRemoteViews(context, maxWidth, minHeight, scale), createRemoteViews(context, minWidth, maxHeight, scale))
            }
        }
    }
    private fun createRemoteViews(context: Context, width: Int, height: Int, scale: Double = 1.0): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_timetable)
        val intent = Intent(context, RootActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.layout, pendingIntent)

        views.setViewVisibility(R.id.placeholder, View.VISIBLE)
        views.setViewVisibility(R.id.table, View.GONE)

        // render views
        val compactMode = tableDisplayRepository.compactMode.value

        views.setViewVisibility(R.id.placeholder, View.VISIBLE)
        views.setViewVisibility(R.id.table, View.GONE)
        tableRepository.currentTable.value?.let { table ->
            val tableView = TimetableView(context, compactMode)

            tableView.theme = runBlocking { themeService.resolveTheme(table) }
            tableView.lectures = table.lectures
            tableView.trimParam = tableDisplayRepository.tableTrimParam.value

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            tableView.measure(width, height)
            tableView.layout(0, 0, width, height)
            tableView.draw(canvas)

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, (width / scale).toInt(), (height / scale).toInt(), false)

            views.setViewVisibility(R.id.placeholder, View.GONE)
            views.setViewVisibility(R.id.table, View.VISIBLE)
            views.setImageViewBitmap(R.id.table, resizedBitmap)
        }

        return views
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
