package com.wafflestudio.snutt.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.model.Notification;
import com.wafflestudio.snutt.model.SettingsItem;

import java.util.List;

/**
 * Created by makesource on 2017. 2. 27..
 */

public class NotificationAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NOTIFICATION_ADAPTER";
    private List<Notification> lists;

    public NotificationAdapter(List<Notification> lists) {
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_notification, parent, false);
        //View view = inflater.inflate(R.layout.cell_notification, null);
        return new NotificationViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //final int itemType = getItemViewType(position);
        ((NotificationViewHolder)holder).bindData(getItem(position));
    }

    public Notification getItem(int position) {
        return lists.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        //SettingsItem item = getItem(position);
        return 0;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "notification list size : " + lists.size());
        return lists.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    private static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView message;

        private NotificationViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message);
        }

        private void bindData(Notification notification) {
            Log.d(TAG, "notification message : " + notification.getMessage());
            message.setText(notification.getMessage());
        }
    }
}
