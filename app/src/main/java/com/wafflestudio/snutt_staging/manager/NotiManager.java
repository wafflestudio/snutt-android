package com.wafflestudio.snutt_staging.manager;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2017. 2. 27..
 */

public class NotiManager {
    private static final String TAG = "NOTIFICATION_MANAGER" ;

    private static NotiManager singleton;
    private SNUTTApplication app;
    private List<Notification> notifications;
    private boolean fetched;

    private NotiManager(SNUTTApplication app) {
        Preconditions.checkNotNull(app);
        this.app = app;
        this.notifications = new ArrayList<>();
        this.fetched = false;
    }

    public static NotiManager getInstance(SNUTTApplication app) {
        singleton = new NotiManager(app);
        return singleton;
    }

    public static NotiManager getInstance() {
        return singleton;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void loadData(int offset, final Callback callback) {
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();

        query.put("limit", 20);
        query.put("offset", offset);
        app.getRestService().getNotification(token, query, new Callback<List<Notification>>() {
            @Override
            public void success(List<Notification> notificationList, Response response) {
                Log.d(TAG, "get notification success!");
                removeProgressBar();
                for (Notification notification : notificationList) {
                    notifications.add(notification);
                }
                if (callback != null) callback.success(notificationList, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "get notification failed.");
                if (callback != null) callback.failure(error);
            }
        });
    }

    public void addProgressBar() {
        notifications.add(null);
    }

    public void removeProgressBar() {
        notifications.remove(notifications.size() - 1);
    }

    public boolean getFetched() {
        return fetched;
    }

    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    public void refreshNotification(final Callback callback) {
        notifications.clear();
        String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        Map query = new HashMap();
        query.put("limit", 20);
        query.put("offset", 0);
        app.getRestService().getNotification(token, query, new Callback<List<Notification>>() {
            @Override
            public void success(List<Notification> notificationList, Response response) {
                Log.d(TAG, "get notification success!");
                for (Notification notification : notificationList) {
                    notifications.add(notification);
                }
                if (callback != null) callback.success(notificationList, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "get notification failed.");
                if (callback != null) callback.failure(error);
            }
        });
    }

}
