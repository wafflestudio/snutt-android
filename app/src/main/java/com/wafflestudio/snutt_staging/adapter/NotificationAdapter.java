package com.wafflestudio.snutt_staging.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.model.Notification;

import java.util.List;

/**
 * Created by makesource on 2017. 2. 27..
 */

public class NotificationAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum VIEW_TYPE {
        Notification(0),
        ProgressBar(1);
        private final int value;
        VIEW_TYPE(int value) {
            this.value = value;
        }
        public final int getValue() {
            return value;
        }
    }
    private static final String TAG = "NOTIFICATION_ADAPTER";
    private List<Notification> lists;


    public NotificationAdapter(List<Notification> lists) {
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE.Notification.getValue()) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_notification, parent, false);
            return new NotificationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cell_progressbar, parent, false);
            return new ProgressBarViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == VIEW_TYPE.Notification.getValue()) {
            ((NotificationViewHolder) holder).bindData(getItem(position));
        }
    }

    public Notification getItem(int position) {
        return lists.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Notification item = lists.get(position);
        return (item == null) ? VIEW_TYPE.ProgressBar.getValue() : VIEW_TYPE.Notification.getValue();
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "notification list size : " + lists.size());
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
           // Log.d(TAG, "notification message : " + notification.getMessage());
            message.setText(notification.getMessage());
        }
    }

    private static class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        private ProgressBarViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }
}
