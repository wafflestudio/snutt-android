package com.wafflestudio.snutt_staging.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.PrefManager;
import com.wafflestudio.snutt_staging.ui.SplashActivity;
import com.wafflestudio.snutt_staging.view.TableView;

/**
 * Created by makesource on 2017. 8. 19..
 */

public class TimetableWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_timetable);
            views.setOnClickPendingIntent(R.id.layout, pendingIntent);
            renderViews(context, appWidgetManager, appWidgetId, views);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void renderViews(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views) {
        int width = (int) SNUTTUtils.getDisplayWidth();
        int height = (int) SNUTTUtils.getDisplayHeight();

        if (!Strings.isNullOrEmpty(PrefManager.getInstance().getCurrentTable())) {
            Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(resultBitmap);
            TableView table = new TableView(context);
            table.drawWidget(canvas, width, height);

            views.setViewVisibility(R.id.placeholder, View.GONE);
            views.setImageViewBitmap(R.id.table, resultBitmap);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }


    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

}
