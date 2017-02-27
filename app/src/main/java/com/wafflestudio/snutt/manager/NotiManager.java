package com.wafflestudio.snutt.manager;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2017. 2. 27..
 */

public class NotiManager {
    private static final String TAG = "NOTIFICATION_MANAGER" ;

    private static NotiManager singleton;
    private List<Notification> notifications;
    private Context context;

    private NotiManager(Context context) {
        Preconditions.checkNotNull(context);
        this.context = context;
    }

    public static NotiManager getInstance(Context context) {
        singleton = new NotiManager(context);
        return singleton;
    }

    public static NotiManager getInstance() {
        return singleton;
    }

    public List<Notification> getNotifications() {
        List<Notification> lists = new ArrayList<>();
        lists.add(new Notification("1", "테스트 1", 1));
        lists.add(new Notification("2", "테스트 2", 1));
        return lists;
    }

}
